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
package toniarts.openkeeper.view.control;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import toniarts.openkeeper.game.component.Owner;
import toniarts.openkeeper.game.map.MapTile;
import toniarts.openkeeper.gui.CursorFactory;
import toniarts.openkeeper.tools.convert.map.ArtResource;
import toniarts.openkeeper.utils.AssetUtils;
import static toniarts.openkeeper.view.map.MapViewController.COLOR_FLASH;
import toniarts.openkeeper.world.animation.AnimationControl;

/**
 * General entity controller, a bridge between entities and view
 *
 * @author Toni Helenius <helenius.toni@gmail.com>
 */
public abstract class EntityViewControl<T, S> extends AbstractControl implements IEntityViewControl<T, S>, AnimationControl {

    private final EntityId entityId;
    private final EntityData entityData;
    private final T data;
    protected S currentAnimation;
    protected S targetAnimation;
    protected final AssetManager assetManager;
    protected boolean isAnimationPlaying = false;

    private boolean active = false;

    public EntityViewControl(EntityId entityId, EntityData entityData, T data, S animation, AssetManager assetManager) {
        this.entityId = entityId;
        this.entityData = entityData;
        this.currentAnimation = animation;
        this.targetAnimation = animation;
        this.assetManager = assetManager;
        this.data = data;
    }

    @Override
    protected void controlUpdate(float tpf) {

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    @Override
    public String getTooltip(short playerId) {
        return "";
    }

    @Override
    public boolean isPickable(short playerId) {
        return true;
    }

    @Override
    public boolean isInteractable(short playerId) {
        return true;
    }

    @Override
    public CursorFactory.CursorType getInHandCursor() {
        return CursorFactory.CursorType.HOLD_THING;
    }

    @Override
    public ArtResource getInHandMesh() {
        return null;
    }

    @Override
    public ArtResource getInHandIcon() {
        return null;
    }

    @Override
    public DroppableStatus getDroppableStatus(MapTile tile, short playerId) {
        return DroppableStatus.NOT_DROPPABLE;
    }

    @Override
    public void drop(MapTile tile, Vector2f coordinates, IEntityViewControl control) {

    }

    @Override
    public void onHover(short playerId) {

    }

    @Override
    public void onHoverStart(short playerId) {
        setHighlight(true);
    }

    @Override
    public void onHoverEnd(short playerId) {
        setHighlight(false);
    }

    private void setHighlight(final boolean enabled) {
        if (active != enabled) {
            active = enabled;
            AssetUtils.setModelHighlight(spatial, COLOR_FLASH, enabled);
        }
    }

    @Override
    public short getOwnerId() {
        return entityData.getComponent(entityId, Owner.class).ownerId;
    }

    @Override
    public void pickUp(short playerId) {

    }

    @Override
    public void interact(short playerId) {

    }

    @Override
    public boolean isSlappable(short playerId) {
        return true;
    }

    @Override
    public void slap(short playerId) {

    }

    @Override
    public EntityId getEntityId() {
        return entityId;
    }

    @Override
    public void onAnimationStop() {
        currentAnimation = null;
        isAnimationPlaying = false;
    }

    @Override
    public void onAnimationCycleDone() {

    }

    @Override
    public boolean isStopAnimation() {
        return targetAnimation == null || targetAnimation != currentAnimation;
    }

    @Override
    public void setTargetAnimation(S state) {
        this.targetAnimation = (S) state;
    }

    @Override
    public T getDataObject() {
        return data;
    }
}