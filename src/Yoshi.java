import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Yoshi extends Entity implements ExecuteAction{

    public Yoshi(){
        super();
    }

    public Yoshi(String id, Point position, List<PImage> images, int resourceLimit,
                     int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit){
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod, health, healthLimit);
    }



    public boolean moveToYoshi(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.position)) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPositionYoshi(world, target.position);


            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public Point nextPositionYoshi(WorldModel world, Point destPos){
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

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, Action.createAnimationAction(this, 0), getAnimationPeriod());

    }
    //TODO: may be an issue with the newHouse() call on line 48
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> yoshiTarget = world.findNearest(this.position, new ArrayList<>(List.of(new Fairy()))); //find nearest fairy


        if (yoshiTarget.isPresent()) {
            Point target = yoshiTarget.get().getPosition();

            if (this.moveToYoshi(world, yoshiTarget.get(), scheduler)) {
                Entity sapling = createSapling(SAPLING_KEY + "_" + yoshiTarget.get().id, target, imageStore.getImageList(SAPLING_KEY), 0);
                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }
            scheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), this.actionPeriod);
        }


    }


