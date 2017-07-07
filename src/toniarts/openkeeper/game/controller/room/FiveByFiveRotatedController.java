/*
 * Copyright (C) 2014-2015 OpenKeeper
 *
 * OpenKeeper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenKeeper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenKeeper.  If not, see <http://www.gnu.org/licenses/>.
 */
package toniarts.openkeeper.game.controller.room;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import toniarts.openkeeper.game.controller.IObjectsController;
import toniarts.openkeeper.utils.Utils;

/**
 * Constructs 5 by 5 "rotated" buildings. As far as I know, only Dungeon Heart
 *
 * @author Toni Helenius <helenius.toni@gmail.com>
 */
public class FiveByFiveRotatedController extends AbstractRoomController implements ICreatureEntrance {

    private static final short OBJECT_HEART_ID = 13;
    private static final short OBJECT_ARCHES_ID = 86;
    private static final short OBJECT_BIG_STEPS_ID = 88;
    private static final short OBJECT_PLUG_ID = 96;

    private final List<Point> spawnPoints = new ArrayList<>(16);

    public FiveByFiveRotatedController(toniarts.openkeeper.common.RoomInstance roomInstance, IObjectsController objectsController) {
        super(roomInstance, objectsController);
    }

    @Override
    public void construct() {
        super.construct();

        // Init the spawn points
        spawnPoints.clear();
        for (Point p : roomInstance.getCoordinates()) {
            if (isTileAccessible(null, null, p.x, p.y)) {
                spawnPoints.add(p);
            }
        }
    }

    @Override
    protected void constructObjects() {

        // We contruct the Dungeon Heart here
        // Because of physics and whatnot, the object are on server, so what about the creation animation?
        // The creation animation should be on the client perhaps... We don't care about it...
        Point center = roomInstance.getCenter();
        objectsController.loadObject(OBJECT_HEART_ID, (short) 0, center.x, center.y);
    }

    @Override
    public boolean isTileAccessible(Integer fromX, Integer fromY, int toX, int toY) {

        // The center 3x3 is not accessible
        Point roomPoint = roomInstance.worldCoordinateToLocalCoordinate(toX, toY);
        return ((roomPoint.x == 0 || roomPoint.x == 4) || (roomPoint.y == 0 || roomPoint.y == 4));
    }

    @Override
    public boolean isDungeonHeart() {
        return true;
    }

    @Override
    public Point getEntranceCoordinate() {

        // FIXME: Is it random truly or just one corner??
        return Utils.getRandomItem(spawnPoints);
    }

}