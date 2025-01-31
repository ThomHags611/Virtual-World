import processing.core.PImage;

import java.util.List;

public class Stump extends Entity{

    public Stump(){super();}
    public Stump(String id, Point position, List<PImage> images, int resourceLimit,
                 int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit){
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod, health, healthLimit);
    }
}
