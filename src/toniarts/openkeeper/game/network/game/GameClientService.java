/*
 * Copyright (C) 2014-2017 OpenKeeper
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
package toniarts.openkeeper.game.network.game;

import com.jme3.math.Vector2f;
import com.jme3.network.service.AbstractClientService;
import com.jme3.network.service.ClientServiceManager;
import com.jme3.network.service.rmi.RmiClientService;
import com.simsilica.es.EntityData;
import com.simsilica.es.client.EntityDataClientService;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import toniarts.openkeeper.game.map.MapData;
import toniarts.openkeeper.game.map.MapTile;
import toniarts.openkeeper.game.network.NetworkConstants;
import toniarts.openkeeper.game.network.streaming.StreamedMessageListener;
import toniarts.openkeeper.game.network.streaming.StreamingClientService;
import toniarts.openkeeper.game.state.session.GameSession;
import toniarts.openkeeper.game.state.session.GameSessionClientService;
import toniarts.openkeeper.game.state.session.GameSessionListener;

/**
 * Client side service for the game lobby services
 *
 * @author Toni Helenius <helenius.toni@gmail.com>
 */
public class GameClientService extends AbstractClientService
        implements GameSessionClientService {

    private static final Logger logger = Logger.getLogger(GameClientService.class.getName());

    private RmiClientService rmiService;
    private GameSession delegate;

    private final GameSessionCallback sessionCallback = new GameSessionCallback();
    private final List<GameSessionListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void loadComplete() {
        getDelegate().loadComplete();
    }

    @Override
    public void loadStatus(float progress) {
        getDelegate().loadStatus(progress);
    }

    @Override
    public void addGameSessionListener(GameSessionListener l) {
        listeners.add(l);
    }

    @Override
    public void removeGameSessionListener(GameSessionListener l) {
        listeners.remove(l);
    }

    @Override
    protected void onInitialize(ClientServiceManager s) {
        logger.log(Level.FINER, "onInitialize({0})", s);
        this.rmiService = getService(RmiClientService.class);
        if (rmiService == null) {
            throw new RuntimeException("LobbyClientService requires RMI service");
        }
        logger.finer("Sharing session callback.");
        rmiService.share(NetworkConstants.GAME_CHANNEL, sessionCallback, GameSessionListener.class);

        // Listen for the streaming messages
        s.getService(StreamingClientService.class).addListener(GameHostedService.MessageType.MAP_DATA.ordinal(), (StreamedMessageListener<MapData>) (MapData data) -> {

            logger.log(Level.FINEST, "onGameDataLoaded({0})", new Object[]{data});
            for (GameSessionListener l : listeners) {
                l.onGameDataLoaded(data);
            }

        });
    }

    /**
     * Called during connection setup once the server-side services have been
     * initialized for this connection and any shared objects, etc. should be
     * available.
     */
    @Override
    public void start() {
        logger.finer("start()");
        super.start();
    }

    private GameSession getDelegate() {
        // We look up the delegate lazily to make the service more
        // flexible.  This way we don't have to know anything about the
        // connection lifecycle and can simply report an error if the
        // game is doing something screwy.
        if (delegate == null) {
            // Look it up
            this.delegate = rmiService.getRemoteObject(GameSession.class);
            logger.log(Level.FINER, "delegate:{0}", delegate);
            if (delegate == null) {
                throw new RuntimeException("No game session found");
            }
        }
        return delegate;
    }

    @Override
    public void selectTiles(Vector2f start, Vector2f end, boolean select) {
        getDelegate().selectTiles(start, end, select);
    }

    @Override
    public void markReady() {
        getDelegate().markReady();
    }

    @Override
    public EntityData getEntityData() {
        return getService(EntityDataClientService.class).getEntityData();
    }

    /**
     * Shared with the server over RMI so that it can notify us about account
     * related stuff.
     */
    private class GameSessionCallback implements GameSessionListener {

        @Override
        public void onGameDataLoaded(MapData mapData) {

            // This is dealt with streaming
//            logger.log(Level.FINEST, "onGameDataLoaded({0})", new Object[]{mapData});
//            for (GameSessionListener l : listeners) {
//                l.onGameDataLoaded(mapData);
//            }
        }

        @Override
        public void onGameStarted() {
            logger.log(Level.FINEST, "onGameStarted()");
            for (GameSessionListener l : listeners) {
                l.onGameStarted();
            }
        }

        @Override
        public void onLoadComplete(short keeperId) {
            logger.log(Level.FINEST, "onLoadComplete({0})", new Object[]{keeperId});
            for (GameSessionListener l : listeners) {
                l.onLoadComplete(keeperId);
            }
        }

        @Override
        public void onLoadStatusUpdate(float progress, short keeperId) {
            logger.log(Level.FINEST, "onLoadStatusUpdate({0},{1})", new Object[]{progress, keeperId});
            for (GameSessionListener l : listeners) {
                l.onLoadStatusUpdate(progress, keeperId);
            }
        }

        @Override
        public void onTilesChange(List<MapTile> updatedTiles) {
            for (GameSessionListener l : listeners) {
                l.onTilesChange(updatedTiles);
            }
        }
    }
}