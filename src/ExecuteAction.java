//implemented by Dude Fairy and Plant
//only used for execute Activity method in Activity
public interface ExecuteAction extends ScheduleAction{
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
}
