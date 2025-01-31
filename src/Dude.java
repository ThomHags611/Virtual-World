import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

//will be extended by Dude_Full and Dude_Not_Full
public abstract class Dude extends Entity implements ExecuteAction{

    public Dude(){
        super();
    }
    public Dude(String id, Point position, List<PImage> images, int resourceLimit,
                     int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit){
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod, health, healthLimit);
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, Action.createAnimationAction(this, 0), getAnimationPeriod());

    }

    public Point nextPositionDude(WorldModel world, Point destPos) {
        PathingStrategy temp = new AStarPathingPath();
        Predicate<Point> canPassThrough = point-> !world.isOccupied(point) || (world.getOccupancyCell(point) instanceof Stump);
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

    public boolean moveToNotFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.position)) {
            this.resourceCount += 1;
            target.health--;
            return true;
        } else {
            Point nextPos = this.nextPositionDude(world, target.position);

            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public boolean moveToFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.position)) {
            return true;
        } else {
            Point nextPos = this.nextPositionDude(world, target.position);

            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public void transformToMario(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        Entity mario = createMario(this.id, this.position, MARIO_ACTION, MARIO_ANIMATION, imageStore.getImageList(MARIO_KEY));

        world.removeEntity(scheduler, this);
        world.addEntity(mario);

        mario.scheduleActions(scheduler, world, imageStore);

    }



}
