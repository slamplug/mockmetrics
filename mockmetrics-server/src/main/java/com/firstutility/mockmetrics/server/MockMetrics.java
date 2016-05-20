package com.firstutility.mockmetrics.server;

import com.firstutility.mockmetrics.filter.MetricFilter;
import com.firstutility.mockmetrics.server.handler.MockMetricsServerHandler;
import com.google.common.util.concurrent.SettableFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
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
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    //private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final Bootstrap bootstrap;

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

        logger.info("create Netty Bootstrap");
        bootstrap = new Bootstrap()
                .group(bossGroup)
                .channel(NioDatagramChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    public void initChannel(NioDatagramChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new MockMetricsServerHandler(metricFilter));
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
                bossGroup.shutdownGracefully(0, 1, TimeUnit.MILLISECONDS);
                //workerGroup.shutdownGracefully(0, 1, TimeUnit.MILLISECONDS);
            }
        }));
    }

    /**
     * Bind all requested ports
     *
     * @param serverPortBindings
     */
    private void bindServerPorts(final List<Integer> serverPortBindings) {
        logger.info("binding server ports");
        for (final Integer port : serverPortBindings) {
            try {
                final SettableFuture<Channel> channelOpened = SettableFuture.create();
                new Thread(() -> {
                    channelFutureList.add(channelOpened);
                    try {
                        Channel channel = bootstrap
                                .bind(port)
                                .addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                        if (channelFuture.isSuccess()) {
                                            channelOpened.set(channelFuture.channel());
                                        } else {
                                            channelOpened.setException(channelFuture.cause());
                                        }
                                    }
                                }).channel();

                        logger.info("Mock Metrics Server started on port: {}",
                                ((InetSocketAddress) channelOpened.get().localAddress()).getPort());

                        channel.closeFuture().syncUninterruptibly();

                    } catch (Exception e) {
                        throw new RuntimeException("Exception while binding Mock Metrics Server on port " + port, e.getCause());
                    }
                }, "Mock Metrics started on port" + port).start();

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
        return !bossGroup.isShuttingDown(); // && !workerGroup.isShuttingDown();
    }

    /**
     * Stop the server
     */
    public void stop() {
        try {
            for (Future<Channel> channelOpened : channelFutureList) {
                channelOpened.get(2, TimeUnit.SECONDS).close(); // close channels
            }
            bossGroup.shutdownGracefully(0, 1, TimeUnit.MILLISECONDS); // stop thread groups
            //workerGroup.shutdownGracefully(0, 1, TimeUnit.MILLISECONDS);

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
