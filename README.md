# mockmetrics

## Connect directly to Mockserver and verify metrics

```
    private MockMetricsServer mockMetricsServer;

    @Before
    public void startAndWaitForMockMetricsServer() {
        mockMetricsServer = MockMetricsServer.startMockMetricsServer(9999);
    }
    
    @Test
    public void shouldTestSomething) throws Exception {
        ...
        ...
        mockMetricsServer.verify(counter().withName("test.metric").withValue(1));
    }

    @After
    public void stopMockMetricsServer() {
        mockMetricsServer.stop();
    }
```

## Use client to connect to Mockserver and verify metrics

```
    private MockMetricsServer mockMetricsServer;
    private MockMetricsClient mockMetricsClient;

    @Before
    public void startAndWaitForMockMetricsServer() {
        mockMetricsServer = MockMetricsServer.startMockMetricsServer(9999);
        mockMetricsClient = MockMetricsClient.startMockMetricsClient("localhost", 9999);
    }
    
    @Test
    public void shouldTestSomething) throws Exception {
        ...
        ...
        mockMetricsClient.verify(verifications().withVerifications(
                verification().withMetric(
                        gauge().withName("test.metric.1").withValue(1)
                ),
                verification().withMetric(
                        gauge().withName("test.metric.2").withValue(2)
                ))
        );
    }

    @After
    public void stopMockMetricsServer() {
        mockMetricsServer.stop();
    }
```
    
