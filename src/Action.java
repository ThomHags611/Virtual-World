/**
 * An action that can be taken by a particular Entity.
 * There are two types of actions in this World:
 * - Activity actions: things like the Sapling growing up, or the DudeNotFull finding a
 *      Tree or Sapling to cut down, or the Fairy finding a Stump to turn into a Sapling.
 * - Animation actions: things like the Dude swinging his axe, or the Tree swaying, or
 *      the Fairy twinkling.
 */


public abstract class Action {

    protected final Entity entity;
    protected final WorldModel world;
    protected final ImageStore imageStore;
    protected final int repeatCount;

    public Action(Entity entity, WorldModel world, ImageStore imageStore, int repeatCount) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;

    }

    public static Action createActivityAction(Entity entity, WorldModel world, ImageStore imageStore) {
        return new Activity(entity, world, imageStore, 0);
    }
    public static Action createAnimationAction(Entity entity, int repeatCount) {
        return new Animation(entity, null, null, repeatCount);
    }

    //if either worldModel is not null we know it is an Activity else it is for Animation

    public abstract void executeAction(EventScheduler scheduler);



    /**
     * Ask the EventScheduler to execute an activity action for this action's Entity.
     * This entails telling the Entity to execute its activity.
     *
     * @param scheduler The scheduler that queues up events.
     */


    /**
     * Ask the EventScheduler to execute an animation action for this action's Entity. This entails
     * telling the Entity to cycle through its images (each animation is one step through its images).
     *
     * @param scheduler The scheduler that queues up events.
     */

    //moved executeActivityAction and Animation's counterpart into the classes so we only need to call this one method for both types

}