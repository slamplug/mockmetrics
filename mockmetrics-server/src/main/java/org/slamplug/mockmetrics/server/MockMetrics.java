package org.slamplug.mockmetrics.server;

import org.slamplug.mockmetrics.filter.MetricFilter;
import org.slamplug.mockmetrics.server.handler.MockMetricsTcpServerHandler;
import org.slamplug.mockmetrics.server.handler.MockMetricsUdpServerHandler;
import com.google.common.util.concurrent.SettableFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class MockMetrics {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // to hold list of channel futures
    private final List<Future<Channel>> channelFutureList = new ArrayList<>();

    // netty
    private final EventLoopGroup udpBossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup tcpBossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup tcpWorkerGroup = new NioEventLoopGroup();

    private final Bootstrap bootstrap;
    private final ServerBootstrap serverBootstrap;

    // filter queue
    private final MetricFilter metricFilter = new MetricFilter();

    /**
     * Create an new MockMetrics
     *
     * @param serverPortBindings
     */
    public MockMetrics(final Integer... serverPortBindings) {
        if (serverPortBindings == null || serverPortBindings.length == 0) {
            throw new IllegalArgumentException("You must specify at least one server port");
        }

        logger.info("create Netty Bootstrap for UDP bindings");
        serverBootstrap = new ServerBootstrap()
                .group(tcpBossGroup, tcpWorkerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline p = nioSocketChannel.pipeline();
                        p.addLast(new HttpRequestDecoder());
                        p.addLast(new HttpResponseEncoder());
                        p.addLast(new MockMetricsTcpServerHandler());
                    }
                });

        bootstrap = new Bootstrap()
                .group(udpBossGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel nioDatagramChannel) throws Exception {
                        ChannelPipeline p = nioDatagramChannel.pipeline();
                        p.addLast(new MockMetricsUdpServerHandler(metricFilter));
                    }
                });

        bindServerPorts(Arrays.asList(serverPortBindings));

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
        }

        // add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                udpBossGroup.shutdownGracefully(0, 1, TimeUnit.MILLISECONDS);
                tcpBossGroup.shutdownGracefully(0, 1, TimeUnit.MILLISECONDS);
                tcpWorkerGroup.shutdownGracefully(0, 1, TimeUnit.MILLISECONDS);
            }
        }));
    }

    /**
     * Bind all UDP requested ports
     *
     * @param serverPortBindings
     */
    private void bindServerPorts(final List<Integer> serverPortBindings) {
        logger.info("binding server ports");
        for (final Integer port : serverPortBindings) {
            try {
                final SettableFuture<Channel> udpChannelOpened = SettableFuture.create();
                new Thread(() -> {
                    channelFutureList.add(udpChannelOpened);
                    try {
                        Channel channel = bootstrap
                                .bind(port)
                                .addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                        if (channelFuture.isSuccess()) {
                                            udpChannelOpened.set(channelFuture.channel());
                                        } else {
                                            udpChannelOpened.setException(channelFuture.cause());
                                        }
                                    }
                                }).channel();

                        logger.info("Mock Metrics Server started on UDP port: {}",
                                ((InetSocketAddress) udpChannelOpened.get().localAddress()).getPort());

                        channel.closeFuture().syncUninterruptibly();

                    } catch (Exception e) {
                        throw new RuntimeException("Exception while binding Mock Metrics Server on port " + port, e.getCause());
                    }
                }, "Mock Metrics started on UDP port" + port).start();

                final SettableFuture<Channel> tcpChannelOpened = SettableFuture.create();
                new Thread(() -> {
                    channelFutureList.add(udpChannelOpened);
                    try {
                        Channel channel = serverBootstrap
                                .bind(port)
                                .addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                        if (channelFuture.isSuccess()) {
                                            tcpChannelOpened.set(channelFuture.channel());
                                        } else {
                                            tcpChannelOpened.setException(channelFuture.cause());
                                        }
                                    }
                                }).channel();

                        logger.info("Mock Metrics Server started on TCP port: {}",
                                ((InetSocketAddress) tcpChannelOpened.get().localAddress()).getPort());

                        channel.closeFuture().syncUninterruptibly();

                    } catch (Exception e) {
                        throw new RuntimeException("Exception while binding Mock Metrics Server on port " + port, e.getCause());
                    }
                }, "Mock Metrics started on TCP port" + port).start();

            } catch (Exception e) {
                throw new RuntimeException("Exception while binding Mock Metrics Server on port " + port, e.getCause());
            }
        }
    }

    /**
     * is server running
     *
     * @return
     */
    public boolean isRunning() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            logger.trace("cannot confirm running status", e);
        }
        return !udpBossGroup.isShuttingDown() && !tcpBossGroup.isShuttingDown() && !tcpWorkerGroup.isShuttingDown();
    }

    /**
     * Stop the server
     */
    public void stop() {
        try {
            for (Future<Channel> channelOpened : channelFutureList) {
                channelOpened.get(2, TimeUnit.SECONDS).close(); // close channels
            }
            udpBossGroup.shutdownGracefully(0, 1, TimeUnit.MILLISECONDS); // stop thread groups
            tcpBossGroup.shutdownGracefully(0, 1, TimeUnit.MILLISECONDS);
            tcpWorkerGroup.shutdownGracefully(0, 1, TimeUnit.MILLISECONDS);

            // wait for socket to be released
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (Exception ie) {
            logger.trace("Exception while stopping Mock Metrics Server", ie);
        }
    }

    /**
     * Get all listening ports
     *
     * @return
     */
    public List<Integer> getPorts() {
        List<Integer> ports = new ArrayList<Integer>();
        for (Future<Channel> channelOpened : channelFutureList) {
            try {
                ports.add(((InetSocketAddress) channelOpened.get(2, TimeUnit.SECONDS).localAddress()).getPort());
            } catch (Exception e) {
                logger.trace("ignoring port for this channel", e);
            }
        }
        return ports;
    }

    /**
     * Get first listening port
     *
     * @return
     */
    public int getPort() {
        for (Future<Channel> channelOpened : channelFutureList) {
            try {
                return ((InetSocketAddress) channelOpened.get(2, TimeUnit.SECONDS).localAddress()).getPort();
            } catch (Exception e) {
                logger.trace("ignoring port for this channel", e);
            }
        }
        return -1;
    }

    /**
     * Get metric filter
     *
     * @return
     */
    public MetricFilter getMetricFilter() {
        return metricFilter;
    }

    // to test
    public static void main(String[] args) {
        new MockMetrics(9999);
    }
}
