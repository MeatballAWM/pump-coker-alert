package PumpCokerAlert;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.GameObject;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
        name = "Pump Coker Alert",
        description = "Alerts when the stove is low on coke.",
        tags = {"pump", "coker", "alert"}
)
public class PumpCokerAlertPlugin extends Plugin
{
    private static final int TARGET_OBJECT_ID = 9085;

    @Inject
    private Client client;

    @Inject
    private Notifier notifier;

    private boolean alreadyNotified = false;

    @Subscribe
    public void onGameTick(GameTick event)
    {
        boolean detected = isObjectDetected(TARGET_OBJECT_ID);

        if (detected && !alreadyNotified)
        {
            alreadyNotified = true;
            notifier.notify("Stove low.");
        }
        else if (!detected)
        {
            alreadyNotified = false;
        }
    }

    private boolean isObjectDetected(int objectId)
    {
        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();
        int plane = client.getPlane();

        for (int x = 0; x < Constants.SCENE_SIZE; x++)
        {
            for (int y = 0; y < Constants.SCENE_SIZE; y++)
            {
                Tile tile = tiles[plane][x][y];

                if (tile == null)
                {
                    continue;
                }

                if (hasId(tile.getGroundObject(), objectId)
                        || hasId(tile.getDecorativeObject(), objectId)
                        || hasId(tile.getWallObject(), objectId))
                {
                    return true;
                }

                GameObject[] gameObjects = tile.getGameObjects();

                if (gameObjects == null)
                {
                    continue;
                }

                for (GameObject gameObject : gameObjects)
                {
                    if (hasId(gameObject, objectId))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean hasId(TileObject object, int objectId)
    {
        return object != null && object.getId() == objectId;
    }
}
