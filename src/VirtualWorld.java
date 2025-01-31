import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;

    public static final int VIEW_WIDTH = 640;
    public static final int VIEW_HEIGHT = 480;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final String DEFAULT_IMAGE_NAME = "background_default";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private String loadFile = "world.sav";
    private long startTimeMillis = 0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(this.world, this.scheduler, this.imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - this.startTimeMillis) * 0.001;
        double frameTime = (appTime - this.scheduler.getCurrentTime());
        this.update(frameTime);
        this.view.drawViewport();
    }

    public void update(double frameTime){
        this.scheduler.updateOnTime(frameTime);
    }


    //will return list of points that will receive background change when mouse is clicked
    public Point[] backgroundEvent(Point pressed){
        int max = 11, min = 7, chance;
        int size = (int)(Math.random()* (max - min + 1) + min); // will give random size between 7-11 -> this will be # of background changes
        int remaining = size;

        Point[] points = new Point[size];

        Point newPoint = world.shiftPoint(pressed); //this will change point if potential background change points are out of bounds

        //loop for as long as we can continue to add more changes
        do {
            for (int x = newPoint.x - 2; x < newPoint.x + 2; x++) {
                for (int y = newPoint.y - 2; y < newPoint.y + 2; y++) {

                    chance = (int) (Math.random() * (max - min + 1) + min); //choose random # between 7-11
                    if (chance % 2 == 0 && remaining > 0) {
                        points[size - remaining] = new Point(x, y);
                        System.out.println(points[size-remaining]);
                        remaining--;

                    }
                }
        }
    }while(remaining > 0);

    return points;

    }
    public void mousePressed() {
        Point pressed = mouseToPoint();


        if(!this.world.isOccupied(pressed)) {

            Point[] backgroundChange = this.backgroundEvent(pressed); //get list of points to change
            for (Point point : backgroundChange) {
                Background temp = new Background("bush", this.imageStore.getImageList("bush"));
                this.world.setBackgroundCell(point, temp);
            }

            Entity yoshi = Entity.createYoshi(Entity.YOSHI_KEY, pressed, Entity.YOSHI_ACTION, Entity.YOSHI_ANIMATION, this.imageStore.getImageList(Entity.YOSHI_KEY));
            Entity yoshi2 = Entity.createYoshi(Entity.YOSHI_KEY, backgroundChange[2], Entity.YOSHI_ACTION, Entity.YOSHI_ANIMATION, this.imageStore.getImageList(Entity.YOSHI_KEY));
            Entity yoshi3 = Entity.createYoshi(Entity.YOSHI_KEY, backgroundChange[5], Entity.YOSHI_ACTION, Entity.YOSHI_ANIMATION, this.imageStore.getImageList(Entity.YOSHI_KEY));

            this.world.addEntity(yoshi);
            this.world.addEntity(yoshi2);
            this.world.addEntity(yoshi3);
            yoshi.scheduleActions(this.scheduler, this.world, this.imageStore);
            yoshi2.scheduleActions(this.scheduler, this.world, this.imageStore);
            yoshi3.scheduleActions(this.scheduler, this.world, this.imageStore);


            Optional<Entity> target = world.findNearest(pressed, new ArrayList<>(List.of(new Dude_Not_Full(), new Dude_Full())));

            //turn nearest dude into a mario
            if (target.isPresent()) {
                try {
                    ((Dude_Full) target.get()).transformToMario(world, scheduler, imageStore);
                } catch (Exception e) {
                    ((Dude_Not_Full) target.get()).transformToMario(world, scheduler, imageStore);

                }
            }
        }
            Optional<Entity> entityOptional = world.getOccupant(pressed);
            if (entityOptional.isPresent()) {
                Entity entity = entityOptional.get();
                System.out.println(entity.getId() + ": " + entity.getClass() + " : " + entity.getHealth());
            }

    }

    public void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.getEntities()) {
            entity.scheduleActions(scheduler, world, imageStore);
        }
    }

    private Point mouseToPoint() {
        return view.getViewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
        }
        if(key == ' ')
        {
            List<Point> temp = new ArrayList<>();
            temp.add(new Point(0, 1));
            temp.add(new Point(0, 2));

        }


    }

    //public static Background markPath()

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            WorldModel.loadImages(in, imageStore,this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load(in, imageStore, createDefaultBackground(imageStore));
        }
    }

    public void parseCommandLine(String[] args) {
        if (args.length != 0) {
            this.loadFile = args[0]; // Instead of the default world.sav
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}
