import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Fairy extends Entity implements ExecuteAction{

    public Fairy(){super();}

    public Fairy(String id, Point position, List<PImage> images, int resourceLimit,
                  int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit){
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod, health, healthLimit);
    }

    public Point nextPositionFairy(WorldModel world, Point destPos){
        PathingStrategy temp = new AStarPathingPath();
        Predicate<Point> canPassThrough = point-> world.withinBounds(point) && !world.isOccupied(point);
        BiPredicate<Point, Point> withinReach = (self, target) -> self.adjacent(target);

        Function<Point, Stream<Point>> potentialNeighbors = point -> Stream.of(new Point(point.x-1,point.y), new Point(point.x,point.y+1),
                                                                               new Point(point.x+1,point.y), new Point(point.x,point.y-1),
                                                                               new Point(point.x+1,point.y+1), new Point(point.x-1,point.y+1),
                                                                               new Point(point.x-1,point.y-1), new Point(point.x+1,point.y-1)).filter(canPassThrough);
        //Point start, Point end, Predicate<Point> canPassThrough, BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>> potentialNeighbors

        //TODO: not sure how to figure out potential neighbors

        List<Point>lists = temp.computePath(this.position, destPos,canPassThrough, withinReach, potentialNeighbors);

        if(lists.isEmpty())
            return this.position;
        return lists.getFirst();

    }

    public boolean moveToFairy(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.position)) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPositionFairy(world, target.position);


            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, Action.createAnimationAction(this, 0), getAnimationPeriod());

    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(this.position, new ArrayList<>(List.of(new Stump())));
        int counter = 0;

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;

            if (this.moveToFairy(world, fairyTarget.get(), scheduler)) {

                Entity sapling = createSapling(SAPLING_KEY + "_" + fairyTarget.get().id, tgtPos, imageStore.getImageList(SAPLING_KEY), 0);
                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), this.actionPeriod);
    }
}
