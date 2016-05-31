package org.slamplug.mockmetrics.server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slamplug.mockmetrics.filter.MetricFilter;
import org.slamplug.mockmetrics.verify.Verifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.slamplug.mockmetrics.verify.Verifications.parseJson;

@ChannelHandler.Sharable
public class MockMetricsTcpServerHandler extends SimpleChannelInboundHandler<Object> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private FullHttpRequest request;

    private MetricFilter metricFilter;

    public MockMetricsTcpServerHandler(final MetricFilter metricFilter) {
        this.metricFilter = metricFilter;
    }

    /**
     * Buffer that stores the response content
     */
    private final StringBuilder buf = new StringBuilder();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        HttpResponseStatus httpResponseStatus = OK;

        logger.info("object type: " + object.getClass().getName());

        if (object instanceof FullHttpRequest) {
            FullHttpRequest request = this.request = (FullHttpRequest) object;

            logger.info("method: [" + request.getMethod() + "] url: [" + request.getUri() + "]");
            if (request.getMethod().equals(HttpMethod.POST)) {

                // Verify metric (Verifications in request)
                if (request.getUri().equals("/verify")) {
                    logger.info("verifying metrics");

                    String body = getBodyAsString(request);
                    Verifications verifications = parseJson(body);

                    logger.info("verificaions: " + verifications.toString());

                    // verify metrics using filter
                    try {
                        buf.setLength(0);
                        this.metricFilter.verify(verifications);
                        buf.append("OK\r\n");
                    } catch (AssertionError e) {
                        buf.append(e.getMessage() + "\r\n");
                    }
                }

                // Reset/clear metric store.
                if (request.getUri().equals("/reset")) {
                    logger.info("reset (clear) stored metrics");
                    this.metricFilter.reset();

                    buf.setLength(0);
                    buf.append("OK\r\n");
                }
            }
        }

        if (object instanceof LastHttpContent) {
            writeResponse(httpResponseStatus, channelHandlerContext);
        }
    }

    /**
     * Write response
     *
     * @param httpResponseStatus
     * @param ctx
     * @return
     */
    private boolean writeResponse(final HttpResponseStatus httpResponseStatus, final ChannelHandlerContext ctx) {
        boolean keepAlive = isKeepAlive(request);
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, httpResponseStatus,
                Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (isKeepAlive(request)) {
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        ctx.write(response);

        return keepAlive;
    }

    private String getBodyAsString(FullHttpRequest fullHttpRequest) {
        StringBuffer sb = new StringBuffer();
        if (fullHttpRequest.content() != null && fullHttpRequest.content().readableBytes() > 0) {
            byte[] bodyBytes = new byte[fullHttpRequest.content().readableBytes()];
            fullHttpRequest.content().readBytes(bodyBytes);
            if (bodyBytes.length > 0) {
                sb.append(new String(bodyBytes, CharsetUtil.ISO_8859_1));
            }
        }
        return sb.toString();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
