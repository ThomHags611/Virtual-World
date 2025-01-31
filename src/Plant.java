public interface Plant extends ExecuteAction{
//implemented by Sapling and Tree

    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);

}
