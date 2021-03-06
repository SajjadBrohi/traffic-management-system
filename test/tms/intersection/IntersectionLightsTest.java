package tms.intersection;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import tms.route.Route;

import java.util.ArrayList;
import java.util.List;

public class IntersectionLightsTest {
    private IntersectionLights intersectionLights;
    private Intersection intersection;

    @Before
    public void setup() {
        intersection = new Intersection("XY");
        Route routeOne = new Route("A", intersection, 50);
        Route routeTwo = new Route("B", intersection, 30);

        List<Route> connections = new ArrayList<>();
        connections.add(routeOne);
        connections.add(routeTwo);

        intersectionLights = new IntersectionLights(connections,2, 3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyConnectionsParameter() {
        List<Route> newConnections = new ArrayList<>();
        IntersectionLights newIntersectionLights
                = new IntersectionLights(newConnections,2, 3);
    }

    @Test
    public void returnYellowTime() {
        Assert.assertEquals(2, intersectionLights.getYellowTime());
    }

    @Test
    public void setNewDuration() {
        intersectionLights.setDuration(5);
        Assert.assertEquals("5", intersectionLights.toString().split(":")[0]);
    }

    @Test(expected = NullPointerException.class)
    public void oneSecondMethod() {
        intersectionLights.oneSecond();
    }

    @Test
    public void toStringMethod() {
        Assert.assertEquals("3:XY,XY", intersectionLights.toString());
    }
}
