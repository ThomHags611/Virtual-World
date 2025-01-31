import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingPath implements PathingStrategy {

    public List<Point> computePaths(Point start, Point end, Predicate<Point> canPassThrough, BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>> potentialNeighbors) {
        List<Point> path = new ArrayList<>();
        List<Point> visited = new ArrayList<>();
        int smallest = 0;

        //will create stream of neighbors we can at our current point and filter out all neighbors that we are unable to visit
        Stream<Point> neighbors = potentialNeighbors.apply(start).filter(canPassThrough);

        path.add(potentialNeighbors.apply(start).min(Comparator.comparingInt(point -> findDistance(point, end))).orElse(start));
        return path;

//        if(canPassThrough.test(new Point(start.x+1, start.y)))
//        {
//            path.add(new Point(start.x+1, start.y));
//        }else if(canPassThrough.test(new Point(start.x, start.y + 1)))
//        {
//            path.add(new Point(start.x, start.y+1));
//
//        }
//
//        return path;
    }

    private int findDistance(Point start, Point end) {
        int dx, dy, diagonalCost = 3, adjacentCost = 2;

        dx = Math.abs(start.x - end.x);
        dy = Math.abs(start.y - end.y);


        //returns the cost to travel to given node
        return diagonalCost * Math.min(dx, dy) + adjacentCost * Math.abs(dx - dy);

    }



    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {

        if(potentialNeighbors.apply(end).filter(canPassThrough).filter(point -> withinReach.test(point, end)).toList().isEmpty())
        {
            return new ArrayList<>();
        }

        PriorityQueue<PointNode> open = new PriorityQueue<>(Comparator.comparingInt(node -> node.fCost)); //sorts nodes from min to max by comparing their fcost
        List<Point> closed = new ArrayList<>(); //list of visited points
        Map<Point, Point> cameFrom = new HashMap<>(); // To build path once end is found. //maps neighbor point -> current point to allow quick backtrack once path is found

        open.add(new PointNode(start, 0, findDistance(start, end)));

        while (!open.isEmpty()) {
            PointNode currentNode = open.poll(); // pop first element of open -> will be smallest fcost position

            Point current = currentNode.point;

            // if goal is reach find path
            if (withinReach.test(current, end)) {
                return reconstructPath(cameFrom, current);
            }

            closed.add(current);

            // Process neighbors
            potentialNeighbors.apply(current) //creates stream with all current neighbors
                    .filter(canPassThrough) //get rid of points we can't traverse
                    .filter(neighbor -> !closed.contains(neighbor) && !open.stream().map(point -> point.getPoint()).toList().contains(neighbor)) //get rid of points already visited or already in open
                    .forEach(neighbor -> { //looping through every element that went through all filters
                        int newGCost = currentNode.gCost + findDistance(current, neighbor); //cost to move to neighbor from current node using current g Cost
                        int newFCost = newGCost + findDistance(neighbor, end); //total cost using newGCost

                        // If the neighbor is not in open or has a better fCost, add it to open
                        if (open.stream().noneMatch(node -> node.point.equals(neighbor) && newGCost <= node.gCost)) {
                            cameFrom.put(neighbor, current);
                            open.add(new PointNode(neighbor, newGCost, newFCost));
                        }
                    });
        }

        // no valid path was found
        return new ArrayList<>();
    }

    // method to reconstruct the path. will only be called once search is done
    private List<Point> reconstructPath(Map<Point, Point> cameFrom, Point current) {
        List<Point> path = new ArrayList<>();
        //key = neighbor value = current (during insertion). So when using a key we get the value of the node that leads to that key
        while (cameFrom.containsKey(current)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path); //reverses list
        return path;
    }

    // Helper class to keep track of f cost and g cost
    private static class PointNode {
        Point point;
        int gCost; // cost from start to current point
        int fCost; // total cost for point taking start and end goal into account

        PointNode(Point point, int gCost, int fCost) {
            this.point = point;
            this.gCost = gCost;
            this.fCost = fCost;
        }

        public Point getPoint() {
            return point;
        }

    }
}
