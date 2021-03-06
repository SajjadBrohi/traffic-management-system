package tms.network;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import tms.util.IntersectionNotFoundException;
import tms.util.InvalidNetworkException;
import tms.util.RouteNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class NetworkInitialiserTest {
    private Network network;

    @Before
    public void setup() throws IOException, InvalidNetworkException {
        network = NetworkInitialiser.loadNetwork("networks/demo.txt");
    }

    @Test (expected = FileNotFoundException.class)
    public void invalidFileLoad() throws IOException, InvalidNetworkException {
        network = NetworkInitialiser.loadNetwork("networks/fail.txt");
    }

    @Test
    public void findIntersectionInUploadedFile() throws IntersectionNotFoundException {
        Assert.assertEquals("W", network.findIntersection("W").getId());
    }

    @Test
    public void findYellowTimeInUploadedFile() {
        Assert.assertEquals(1, network.getYellowTime());
    }

    @Test
    public void getConnectionInUploadedFile() throws IntersectionNotFoundException, RouteNotFoundException {
        Assert.assertEquals("X:Y:60:0", network.getConnection("X", "Y").toString());
    }

    @Test
    public void getCongestionInUploadedFile() throws IntersectionNotFoundException, RouteNotFoundException {
        Assert.assertEquals(0, network.getCongestion("X", "Y"));
    }

    @Test
    public void checkEmptyFile() throws IOException, InvalidNetworkException {
        network = NetworkInitialiser.loadNetwork("networks/test2.txt");
    }

    @Test
    public void checkEquality() throws IntersectionNotFoundException {
        Network newNetwork = network;
        Assert.assertEquals(true, newNetwork == network);
    }

    @Test
    public void checkHashCodeSimilarity() throws IntersectionNotFoundException {
        Network newNetwork = network;
        Assert.assertEquals(true, newNetwork.hashCode() == network.hashCode());
    }
}
