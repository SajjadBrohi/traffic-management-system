package tms.intersection;

import tms.network.NetworkInitialiser;
import tms.route.Route;
import tms.util.InvalidOrderException;
import tms.util.RouteNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a point at which routes can originate and terminate.
 * <p>
 * All intersections have a unique identifier (ID), a list of incoming
 * connections and, optionally, a set of traffic lights.
 * @ass1_2
 */
public class Intersection {
    /** Unique identifier for this intersection. */
    private String id;
    /** List of routes that terminate here. */
    private List<Route> incomingConnections;
    private IntersectionLights intersectionLights;
    /**
     * Amount by which to reduce the speed limit of speed signs on incoming
     * routes.
     */
    private static final int SPEED_REDUCTION_AMOUNT = 10;
    /**
     * Speed signs with a speed limit below this amount will not have their
     * displayed speed reduced.
     */
    private static final int SPEED_REDUCTION_CUTOFF = 50;

    /**
     * Creates a new intersection with the given identifier.
     *
     * @param id a unique string identifier
     * @ass1
     */
    public Intersection(String id) {
        this.id = id;
        this.incomingConnections = new ArrayList<>();
        intersectionLights = null;
    }

    /**
     * Returns the ID of this intersection.
     *
     * @return the ID
     * @ass1
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns a new list containing all the incoming connections to this
     * intersection.
     * <p>
     * Adding/removing routes from this list should not affect the
     * intersection's internal list of connecting routes.
     *
     * @return list of all connecting routes to this intersection
     * @ass1
     */
    public List<Route> getConnections() {
        return new ArrayList<>(this.incomingConnections);
    }

    /**
     * Returns true if this intersection has a set of traffic lights;
     * false otherwise.
     *
     * @return whether this intersection has traffic lights
     */
    public boolean hasTrafficLights() {
        if (intersectionLights != null) {
            return true;
        }

        return  false;
    }

    /**
     * Gets a list containing all intersections that have incoming routes to
     * this intersection.
     * <p>
     * If no such intersections exist, return an empty list.
     *
     * @return a list of all intersections that feed a route that ends at this
     * intersection
     * @ass1
     */
    public List<Intersection> getConnectedIntersections() {
        List<Intersection> connectedIntersections = new ArrayList<>();
        for (Route route : incomingConnections){
            connectedIntersections.add(route.getFrom());
        }
        return connectedIntersections;
    }

    /**
     * Sets the duration of each green-yellow cycle for this
     * intersection's traffic lights.
     *
     * @param duration new duration to set
     * @throws IllegalStateException if this intersection has no traffic lights
     * @throws IllegalArgumentException if duration is less than the traffic
     * lights' yellow time plus one
     */
    public void setLightDuration(int duration) {
        if (!hasTrafficLights()) {
            throw new IllegalStateException();
        }

        if (duration < intersectionLights.getYellowTime() + 1) {
            throw new IllegalArgumentException();
        }

        intersectionLights.setDuration(duration);
    }

    /**
     * Adds traffic lights to this intersection the given route order.
     *
     * @param order order of incoming routes to turn traffic lights green
     * @param yellowTime time for which traffic lights appear yellow
     * @param duration time for which traffic lights appear green and yellow
     * @throws IllegalArgumentException if yellowTime < 1; or if
     * duration < yellowTime + 1
     * @throws InvalidOrderException if the given order is not a
     * permutation or incomingRoutes; or if order is empty
     * lights' yellow time plus one
     */
    public void addTrafficLights(List<Route> order,
                                  int yellowTime,
                                  int duration)
            throws InvalidOrderException,
            IllegalArgumentException {

        if (yellowTime < 1 || duration < yellowTime + 1) {
            throw new IllegalArgumentException();
        }

        // Check if given order is not a permutation of incomingRoutes
        boolean permutationCheck = false;
        for (Route routeInOrder: order) {
            for (Route routeInConnections: incomingConnections) {
                if (routeInConnections.equals(routeInOrder)) {
                    permutationCheck = true;
                }
            }

            if (permutationCheck) {
                permutationCheck = false;
            } else {
                throw new InvalidOrderException();
            }
        }

        if (order.isEmpty()) {
            throw new InvalidOrderException();
        }

        for (Route route: order) {
            route.addTrafficLight();
        }

        this.intersectionLights = new IntersectionLights(
                order,
                yellowTime,
                duration
        );
    }

    /**
     * Creates a new Route originating from the given intersection and adds it
     * to our list of incoming routes.
     * <p>
     * The ID of the new route should be of the form "from:to" where 'from' is
     * the ID of the origin intersection and 'to' is the ID of this
     * intersection. The new route should have a default speed given by
     * 'defaultSpeed', however if this value is negative, then an exception
     * should be thrown and no new connection should be added.
     * <p>
     * If this intersection has an IntersectionLights a new traffic light signal
     * should be added to the new route.
     * <p>
     * If there is already a connection from 'from', then instead of creating a
     * new connection, an IllegalStateException should be thrown and the method
     * should do nothing.
     *
     * @param from the intersection the connection is from
     * @param defaultSpeed the connecting route's default speed
     * @throws IllegalStateException if a route already exists connecting this
     * intersection and the given intersection
     * @throws IllegalArgumentException if the given default speed is negative
     * @ass1_2
     */
    public void addConnection(Intersection from, int defaultSpeed)
            throws IllegalStateException {
        if (defaultSpeed < 0) {
            throw new IllegalArgumentException("Speed must be positive");
        }
        if (getConnectedIntersections().contains(from)) {
            throw new IllegalStateException(
                    "Connection already exists from intersection: \""
                            + from.getId() + "\"");
        }
        Route newRoute = new Route(
                from + NetworkInitialiser.LINE_INFO_SEPARATOR + id,
                from, defaultSpeed);
        incomingConnections.add(newRoute);
        if (hasTrafficLights()) {
            newRoute.addTrafficLight();
        }
    }

    /**
     * Reduces the speed limit on incoming routes to this intersection.
     * <p>
     * All incoming routes with an electronic speed sign should have their
     * speed limit changed to be the greater of 50 and the current displayed
     * speed minus 10.
     * <p>
     * Routes without an electronic speed sign should not be affected.
     * <p>
     * No speed limits should be <i>increased</i> as a result of calling this
     * method, i.e. routes with a speed limit of 50 or below should not be
     * affected.
     * @ass1
     */
    public void reduceIncomingSpeedSigns() {
        for (Route route : incomingConnections) {
            if (!route.hasSpeedSign()) {
                continue;
            }
            int currentSpeed = route.getSpeed();
            if (currentSpeed >= SPEED_REDUCTION_CUTOFF) {
                route.setSpeedLimit((Math.max(SPEED_REDUCTION_CUTOFF,
                        currentSpeed - SPEED_REDUCTION_AMOUNT)));
            }
        }
    }

    /**
     * Given an origin intersection, returns the route that connects it to this
     * destination intersection.
     *
     * @param from an intersection that is connected to this intersection
     * @return the route that goes from 'from' to this intersection
     * @throws RouteNotFoundException if no route exists from the given
     * intersection to this intersection
     * @ass1
     */
    public Route getConnection(Intersection from)
        throws RouteNotFoundException {
    for (Route route : incomingConnections) {
        if (route.getFrom().equals(from)) {
            return route;
        }
    }
    throw new RouteNotFoundException("Route not found from \""
            + from.getId() + "\" to \"" + this.getId() + "\"");
    }

    /**
     * Returns true if and only if this intersection is equal to the
     * other given intersection.
     *
     * @param o other object to compare equality
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Intersection that = (Intersection) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Returns the hash code of this intersection.
     *
     * @return Returns the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns the string representation of this intersection.
     * <p>
     * The format of the string to
     * return is simply "id" where 'id' is this intersection's identifier
     * string.
     * <p>
     * For example, an intersection with the an ID of "ABC" and traffic lights
     * with a string representation of "3:X,Y,Z" would have a toString() value
     * of "ABC:3:X,Y,Z".
     *
     * @return string representation of this intersection
     * @ass1_2
     */
    @Override
    public String toString() {
        if (!hasTrafficLights()) {
            return this.id;
        }
        return this.id + ":" + intersectionLights.toString();
    }
}
