package org.slamplug.mockmetrics.server.handler;

import org.slamplug.mockmetrics.filter.MetricFilter;
import org.slamplug.mockmetrics.handler.MetricHandler;
import org.slamplug.mockmetrics.model.Metric;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Handles server side channel
 */
public class MockMetricsUdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MetricFilter metricFilter;

    public MockMetricsUdpServerHandler(final MetricFilter metricFilter) {
        this.metricFilter = metricFilter;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        logger.info("channelRead0: " + packet);

        logger.info("packet content..." + getPacketContent(packet.content()));
        List<Metric> metricList = MetricHandler.handleMetrics(getPacketContent(packet.content()).split("\n"));
        logger.info("metricList...." + metricList);

        metricFilter.onMetrics(metricList);
    }


    /**
     * get bytes from datagram packet
     *
     * @param byteBuf
     * @return
     */
    private String getPacketContent(final ByteBuf byteBuf) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteBuf.capacity(); i++) {
            if (byteBuf.getByte(i) != 0) {
                sb.append((char) byteBuf.getByte(i));
            }
        }
        return StringUtils.removeEnd(sb.toString(), "\n");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause.toString());
    }
}
