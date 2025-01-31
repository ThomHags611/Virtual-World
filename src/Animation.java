public class Animation extends Action{

    public Animation(Entity entity, WorldModel world, ImageStore imageStore, int repeatCount) {
        super(entity, null, null, repeatCount);
    }

    public void executeAction(EventScheduler scheduler) {
        this.entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.entity, createAnimationAction(this.entity, Math.max(this.repeatCount - 1, 0)), this.entity.getAnimationPeriod());
        }
    }

}
