import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Mario extends Entity implements ExecuteAction{

    private int counter = 3;

    public Mario(String id, Point position, List<PImage> images, int resourceLimit,
                 int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit){
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod, health, healthLimit);
    }



    public boolean moveToMario(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.position)) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPositionMario(world, target.position);


            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public Point nextPositionMario(WorldModel world, Point destPos){
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

        Optional<Entity> marioTarget = world.findNearest(this.position, new ArrayList<>(List.of(new Yoshi()))); //find nearest yoshi


        if (marioTarget.isPresent()) {
            Point target = marioTarget.get().getPosition();

            if (this.moveToMario(world, marioTarget.get(), scheduler)) {
                Entity fairy = createFairy(FAIRY_KEY + "_" + marioTarget.get().id, target,FAIRY_ACTION, FAIRY_ANIMATION, imageStore.getImageList(FAIRY_KEY));
                world.addEntity(fairy);
                fairy.scheduleActions(scheduler, world, imageStore);
                counter--;
                //this.transformToDude(world, scheduler, imageStore);

            }
        }
        if(counter == 0){
             this.transformToDude(world, scheduler, imageStore);
             counter--;

        }
        if(counter > 0)
        scheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), this.actionPeriod);
    }

    public void transformToDude(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        Entity dude_not_full = createDudeNotFull(this.id, this.position, DUDE_ACTION, DUDE_ANIMATION, 2, imageStore.getImageList(DUDE_KEY));
        world.removeEntity(scheduler, this);
        world.addEntity(dude_not_full);

        dude_not_full.scheduleActions(scheduler, world, imageStore);

    }


}