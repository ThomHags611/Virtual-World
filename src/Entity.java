import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
//Entity is now an abstract class that will be extended by previous EntityKind Values
public abstract class Entity{
    /*
        Static variables: These do not need to made private. But you should move them to new classes if appropriate.

        Variables whose names end in "IDX" are indices, i.e., positions. For example, DUDE_NUM_PROPERTIES indicates
        that Dudes have 3 properties (in addition to their id, x position, y position). The DUDE_ACTION_PERIOD_IDX (0)
        indicates that the Dude's action period is its first property, DUDE_ANIMATION_PERIOD_IDX (1) indicates that
        the Dude's animation period is its second property, and so on.
     */


    // The Sapling's action and animation periods have to be in sync since it grows and gains health at same time.
    public static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000;
    public static final int SAPLING_HEALTH_LIMIT = 5;
    public static final String STUMP_KEY = "stump";
    public static final int STUMP_NUM_PROPERTIES = 0;
    public static final String SAPLING_KEY = "sapling";
    public static final int SAPLING_HEALTH_IDX = 0;
    public static final int SAPLING_NUM_PROPERTIES = 1;
    public static final String OBSTACLE_KEY = "obstacle";
    public static final int OBSTACLE_ANIMATION_PERIOD_IDX = 0;
    public static final int OBSTACLE_NUM_PROPERTIES = 1;
    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD_IDX = 0;
    public static final int DUDE_ANIMATION_PERIOD_IDX = 1;
    public static final int DUDE_RESOURCE_LIMIT_IDX = 2;
    public static final int DUDE_NUM_PROPERTIES = 3;
    public static final String HOUSE_KEY = "house";
    public static final int HOUSE_NUM_PROPERTIES = 0;
    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD_IDX = 0;
    public static final int FAIRY_ACTION_PERIOD_IDX = 1;
    public static final int FAIRY_NUM_PROPERTIES = 2;
    public static final String TREE_KEY = "tree";
    public static final int TREE_ANIMATION_PERIOD_IDX = 0;
    public static final int TREE_ACTION_PERIOD_IDX = 1;
    public static final int TREE_HEALTH_IDX = 2;
    public static final int TREE_NUM_PROPERTIES = 3;
    public static final double TREE_ANIMATION_MAX = 0.600;
    public static final double TREE_ANIMATION_MIN = 0.050;
    public static final double TREE_ACTION_MAX = 1.400;
    public static final double TREE_ACTION_MIN = 1.000;
    public static final int TREE_HEALTH_MAX = 3;
    public static final int TREE_HEALTH_MIN = 1;
    public static final String FROZEN_KEY = "frozen";
    public static final int FROZEN_ANIMATION_PERIOD_IDX = 0;
    public static final int FROZEN_ACTION_PERIOD_IDX = 1;
    public static final int FROZEN_NUM_PROPERTIES = 2;
    public static final String YOSHI_KEY = "yoshi";
    public static final int YOSHI_ANIMATION_PERIOD_IDX = 0;
    public static final int YOSHI_ACTION_PERIOD_IDX = 1;
    public static final int YOSHI_NUM_PROPERTIES = 2;
    public static final String MARIO_KEY = "mario";

    public static final double YOSHI_ANIMATION = .08;
    public static final double YOSHI_ACTION = .142;
    public static final double FAIRY_ANIMATION = .123;
    public static final double FAIRY_ACTION = .123;
    public static final double DUDE_ANIMATION = .180;
    public static final double DUDE_ACTION = .848;
    public static final double MARIO_ANIMATION = .115;
    public static final double MARIO_ACTION = .234;
    // Instance variables

    protected final String id;
    protected Point position;
    protected final List<PImage> images;
    protected int imageIndex;
    protected final int resourceLimit;
    protected int resourceCount;
    protected final double actionPeriod;
    protected final double animationPeriod;
    protected int health;
    protected final int healthLimit;


    public Entity(){
        this.id = null;
        this.position = null;
        this.images = null;
        this.imageIndex = 0;
        this.resourceLimit = 0;
        this.resourceCount = 0;
        this.actionPeriod = 0;
        this.animationPeriod = 0;
        this.health = 0;
        this.healthLimit = 0;
    }
    /**
     * Creates a new Entity.
     * @param id The id of the new entity.
     * @param position The position (x,y coordinate) of this new entity.
     * @param images The image list associated with this entity.
     * @param resourceLimit The resourceLimit for this entity. Not all entities need this.
     * @param resourceCount The resourceCount for this entity. Not all entities need this.
     * @param actionPeriod The actionPeriod for this entity (i.e., how long it takes to perform each activity action).
     *                     Not all entities need this.
     * @param animationPeriod The animationPeriod (i.e., how long it takes to perform one animation).
     *                        Not all entities need this.
     * @param health The entity's starting health. Not all entities need this.
     * @param healthLimit The entity's upper health limit. Not all entities need this.
     */



    public Entity(String id, Point position, List<PImage> images, int resourceLimit,
                  int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }

    public static Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.getFirst();
            int nearestDistance = nearest.position.distanceSquared(pos);

            for (Entity other : entities) {
                int otherDistance = other.position.distanceSquared(pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getHealth() {
        return health;
    }

    public PImage getCurrentImage(){
        return this.images.get(this.imageIndex % this.images.size());
    }



    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }

    //created ScheduleAction Interface, all classes below implement one method(this one) and run their case
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {

        if (this instanceof ScheduleAction) {
            this.scheduleActions(scheduler, world, imageStore);
        }

    }


    //if object is an instanceof ScheduleAction then run the switch else use default
    public double getAnimationPeriod() {
            if(this instanceof ScheduleAction){
                return this.animationPeriod;
            }else{
                throw new UnsupportedOperationException(String.format("getAnimationPeriod not supported for %s", this.getClass()));
            }
        }


    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    /**
     * Creates a new House.
     * @param id The new House's id.
     * @param position The new House's position (x,y coordinate) in the World.
     * @param images Images to use for House.
     * @return a new Entity whose type is House.
     */
    public static Entity createHouse(String id, Point position, List<PImage> images) {
        return new House(id, position, images, 0, 0, 0, 0, 0, 0);
    }

    /**
     * Creates a new Obstacle.
     * @param id The new Obstacle's id.
     * @param position The Obstacle's x,y position in the World.
     * @param animationPeriod The time (seconds) taken for each animation.
     * @param images Images to use for the Obstacle.
     * @return a new Entity whose type is Obstacle.
     */
    public static Entity createObstacle(String id, Point position, double animationPeriod, List<PImage> images) {
        return new Obstacle(id, position, images, 0, 0, 0, animationPeriod, 0, 0);
    }

    /**
     * Creates a new Tree.
     * @param id The new Tree's id.
     * @param position The Tree's x,y position in the World.
     * @param actionPeriod The time (seconds) taken for each activity (checking its health).
     * @param animationPeriod The time (seconds) taken for each animation.
     * @param health The Tree's starting health.
     * @param images Images to use for the Tree.
     * @return a new Entity whose type is Tree.
     */
    public static Entity createTree(String id, Point position, double actionPeriod, double animationPeriod, int health, List<PImage> images) {
        return new Tree(id, position, images, 0, 0, actionPeriod, animationPeriod, health, 0);
    }

    /**
     * Creates a new Stump.
     * @param id The new Stump's id.
     * @param position The Stump's x,y position in the world.
     * @param images Images to use for the Stump.
     * @return a new Entity whose type is Stump.
     */
    public static Entity createStump(String id, Point position, List<PImage> images) {
        return new Stump(id, position, images, 0, 0, 0, 0, 0, 0);
    }

    /**
     * Creates a new Sapling.
     * @param id The new Sapling's id.
     * @param position The Sapling's x,y position in the World.
     * @param images Images to use for the Sapling.
     * @param health The Sapling's starting health.
     *               Note that the Sapling also has an upper health limit, after which it will turn into a tree.
     *               Like the Tree, the Sapling also has an action and animation period, but those are not parameters
     *               since they need to be kept in sync with each other. The Sapling's activity is to grow.
     * @return a new Entity whose type is Sapling.
     */
    public static Entity createSapling(String id, Point position, List<PImage> images, int health) {
        return new Sapling(id, position, images, 0, 0, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, SAPLING_HEALTH_LIMIT);
    }

    /**
     * Creates a new Fairy.
     * @param id The Fairy's id
     * @param position The Fairy's x,y location in the World.
     * @param actionPeriod The time (seconds) taken for each activity (turning a Stump into a Sapling).
     * @param animationPeriod The time (seconds) taken for each animation.
     * @param images Images to use for the Fairy.
     * @return a new Entity whose type is Fairy.
     */
    public static Entity createFairy(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Fairy(id, position, images, 0, 0, actionPeriod, animationPeriod, 0, 0);
    }

    /**
     * Creates a new DudeNotFull.
     * @param id The Dude's id.
     * @param position The Dude's x,y position in the World.
     * @param actionPeriod The time (seconds) taken for each activity (finding and chopping down a tree or sapling.)
     * @param animationPeriod The time (seconds) taken for each animation.
     * @param resourceLimit The amount of wood this Dude can carry before becoming a DudeFull.
     * @param images Images to use for the Dude.
     * @return a new Entity whose type is DudeNotFull.
     */
    public static Entity createDudeNotFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new Dude_Not_Full(id, position, images, resourceLimit, 0, actionPeriod, animationPeriod, 0, 0);
    }

    /**
     * Creates a new DudeFUll
     * @param id The Dude's id.
     *           If a DudeNotFull turns into a DudeFull, it will still have the same id.
     * @param position The Dude's x,y position in the World.
     * @param actionPeriod The time (seconds) taken for each activity (going to the House and turning into a DudeNotFull).
     * @param animationPeriod The time (seconds) taken for each animation.
     * @param resourceLimit The amount of wood this Dude can carry.
     * @param images Images to use for the Dude.
     * @return a new Entity whose type is DudeFull.
     */
    public static Entity createDudeFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new Dude_Full(id, position, images, resourceLimit, 0, actionPeriod, animationPeriod, 0, 0);
    }



    public static Entity createFrozen(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new Frozen(id, position, images, resourceLimit, 0, actionPeriod, animationPeriod, 0, 0);
    }
    public static Entity createYoshi(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Yoshi(id, position, images, 0, 0, actionPeriod, animationPeriod, 0, 0);
    }

    public static Entity createMario(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Mario(id, position, images, 0, 0, actionPeriod, animationPeriod, 0, 0);
    }
}
