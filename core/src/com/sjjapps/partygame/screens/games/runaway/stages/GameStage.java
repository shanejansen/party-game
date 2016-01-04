package com.sjjapps.partygame.screens.games.runaway.stages;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sjjapps.partygame.Game;
import com.sjjapps.partygame.common.WidgetFactory;
import com.sjjapps.partygame.common.models.Point;
import com.sjjapps.partygame.common.models.User;
import com.sjjapps.partygame.screens.games.runaway.actors.Player;
import com.sjjapps.partygame.screens.games.runaway.models.GameUser;

import java.util.Random;

/**
 * Created by Shane Jansen on 12/21/15.
 */
public class GameStage extends Stage {
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 800;
    private static final float SPEED_MULTIPLIER = 5;
    private GameStageInterface mInterface;
    private Touchpad mTouchpad;
    private Random mRandom;
    private float mBoundsWidth, mBoundsHeight;
    private GameUser mGameUser;
    private Player mPlayer;
    private Array<Player> mPlayers;

    public static void addAssets() {
        Player.addAssets();
        WidgetFactory.addAssets();
    }

    public interface GameStageInterface {
        void playerMoved(GameUser gameUser);
    }

    public GameStage(GameStageInterface gameStageInterface, Touchpad touchpad) {
        super(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT), Game.SPRITE_BATCH);
        mInterface = gameStageInterface;
        mTouchpad = touchpad;
        mRandom = new Random();
        mBoundsWidth = WORLD_WIDTH;
        mBoundsHeight = WORLD_HEIGHT;
        User thisUser = Game.NETWORK_HELPER.findThisUser();
        mGameUser = new GameUser(thisUser.getId());

        // Create players
        BitmapFont bitmapFont = WidgetFactory.getInstance().getCustomBitmapFont(30);
        mPlayer = new Player(thisUser, bitmapFont);
        addActor(mPlayer);
        Point initialPos = randomBoundedPoint((int) WORLD_WIDTH, (int) WORLD_HEIGHT);
        mPlayer.setPosition(initialPos.x, initialPos.y);
        mPlayers = new Array<Player>();
        for (User u: Game.NETWORK_HELPER.users.getUsers()) {
            Player player = new Player(u, bitmapFont);
            player.setPosition(-WORLD_WIDTH, -WORLD_HEIGHT); // Start off screen
            addActor(player);
            mPlayers.add(player);
        }

        // Initial update
        mGameUser.setPosY(mPlayer.getY());
        mGameUser.setPosX(mPlayer.getX());
        mInterface.playerMoved(mGameUser);
    }

    /**
     * Loop through each Player and check to see if the User
     * id is the same as the GameUser id. If there is a match,
     * update the Player's position based on gameUser.
     * @param gameUser
     */
    public void updatePlayer(GameUser gameUser) {
        for (Player p: mPlayers) {
            if (p.getUser().getId() == gameUser.getUserId()) {
                p.setPosition(gameUser.getPosX(), gameUser.getPosY());
            }
        }
    }

    public Point randomBoundedPoint(int maxX, int maxY) {
        return new Point(mRandom.nextInt(maxX), mRandom.nextInt(maxY));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        //if (mPlayer.getX() > ) {

        //}
        mPlayer.setX(mPlayer.getX() + mTouchpad.getKnobPercentX() * SPEED_MULTIPLIER);
        mPlayer.setY(mPlayer.getY() + mTouchpad.getKnobPercentY() * SPEED_MULTIPLIER);
        mGameUser.setPosX(mPlayer.getX());
        mGameUser.setPosY(mPlayer.getY());
        mInterface.playerMoved(mGameUser);
    }

    @Override
    public void draw() {
        // Test circles
        Game.SHAPE_RENDERER.setProjectionMatrix(getViewport().getCamera().combined);
        Game.SHAPE_RENDERER.begin(ShapeRenderer.ShapeType.Filled);
        Game.SHAPE_RENDERER.setColor(Color.YELLOW);
        Game.SHAPE_RENDERER.circle(getViewport().getWorldWidth(), getViewport().getWorldHeight(), 30);
        Game.SHAPE_RENDERER.circle(0, 0, 30);
        Game.SHAPE_RENDERER.end();

        super.draw();
    }
}
