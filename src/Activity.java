public class Activity extends Action {

    public Activity(Entity entity, WorldModel world, ImageStore imageStore, int repeatCount) {
        super(entity, world, imageStore, repeatCount);
    }



    public void executeAction(EventScheduler scheduler) {

        if(this.entity instanceof ExecuteAction)
            ((ExecuteAction)this.entity).executeActivity(this.world, this.imageStore, scheduler);
            else
                    throw new UnsupportedOperationException(String.format("executeActivityAction not supported for %s", this.entity.getClass()));
        }
}
