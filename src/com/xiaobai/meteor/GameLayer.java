package com.xiaobai.meteor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabelAtlas;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

public class GameLayer extends CCLayer {
	private static final String TAG = "GameLayer";
	private CCSprite sprite;
	private CCSprite back;
	// 图层宽度
	private static final int LAYER_WIDTH = 640;
	// 图层高度
	private static final int LAYER_HEIGHT = 960;
	// 屏幕宽度以及高度
	private float mMaxX = 0.0f;
	private float mMaxY = 0.0f;

	// 画布缩放比例
	private float mScaleX = 0.0f;
	private float mScaleY = 0.0f;
	// 已经坚持的时间
	private int mScore = 0;
	// 计时板
	private CCLabelAtlas mTimerBoard = null;
	Random rand = new Random();

	private List<CCSprite> mTargets = new ArrayList<CCSprite>();

	protected GameLayer() {
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		mMaxX = winSize.width;
		mMaxY = winSize.height;
		mScaleX = mMaxX / LAYER_WIDTH;
		mScaleY = mMaxY / LAYER_HEIGHT;
		this.setAnchorPoint(CGPoint.ccp(0.0f, 0.0f));
		this.setScaleX(mScaleX);
		this.setScaleY(mScaleY);
		init();
		this.setIsAccelerometerEnabled(true);
		this.schedule("gameLogic", 1.0f / 60.0f);
		this.schedule("scoreLoop", 1f);
	}

	public void gameLogic(float dt) {
		if (isAddTargetAvailable()) {
			addTarget();
		}
		updatePos();
		CGPoint point = checkCollision();
		if (point != null) { // 碰撞
			sprite.setVisible(false);
			this.unschedule("gameLogic");
			this.unschedule("scoreLoop");
			showExplodeAt(point);
			this.schedule("gameOver", 1.0f);
		}
	}
	
	// 结束，并进入开始界面
	public void gameOver(float dt){
		this.pauseSchedulerAndActions();
		showGameOverBoard();
	}
	
	private void showGameOverBoard(){
		LogUtils.LOG(TAG, "ex showGameOverBoard");
		CCSprite gameOver = new GameOverSprite(mScore);
		gameOver.setPosition(LAYER_WIDTH / 2, LAYER_HEIGHT / 2);
		gameOver.setVisible(true);
		addChild(gameOver, 10);
	}

	public static CCScene scene() {
		CCScene scene = CCScene.node();
		GameLayer layer = new GameLayer();
		scene.addChild(layer);
		return scene;
	}

	// 初始化计时器
	private void initTimerBoard() {
		String str = mScore + "";
		mTimerBoard = CCLabelAtlas.label(str, "number.png", 33, 50, '.');
		mTimerBoard.setAnchorPoint(CGPoint.zero());
		mTimerBoard.setPosition(CGPoint.ccp(70, LAYER_HEIGHT - 60));
		addChild(mTimerBoard, 0);
	}

	// 显示当前时间
	public void scoreLoop(float dt) {
		mScore += 10;
		String str = mScore + "";
		mTimerBoard.setString(str);
	}

	// 在指定位置显示爆炸效果
	private void showExplodeAt(CGPoint point) {
		LogUtils.d("[[Show Explode]] " + point.x + ", " + point.y);
		CCSprite explodeSprite = CCSprite.sprite("explode.png");
		explodeSprite.setPosition(point);
		float nWidth = 95;
		float nHeight = 95;
		explodeSprite.setTextureRect(CGRect.make(0, 0, nWidth, nHeight));
		CCAnimation animation = CCAnimation.animation("explore");
		for (int i = 0; i < 11; i++) {
			animation.addFrame(explodeSprite.getTexture(),
					CGRect.make(i * nWidth, 0, nWidth, nHeight));
		}
		addChild(explodeSprite);
		CCAnimate animate = CCAnimate.action(1, animation, false);
		explodeSprite.runAction(animate);
	}

	private CGPoint checkCollision() {
		CGRect rect = sprite.getTextureRect();
		CGPoint pos = sprite.getPosition();
		// 缩小碰撞检测区域，尽量使得飞机和炸弹贴上了再爆炸
		float scale = 0.9f;
		CGRect dstRect = CGRect.make(pos.x - (rect.size.width * scale) / 2,
				pos.y - (rect.size.height * scale) / 2,
				rect.size.width * scale, rect.size.height * scale);
		for (CCSprite target : mTargets) {
			if (CGRect.containsPoint(dstRect, target.getPosition())) {
				return target.getPosition();
			}
		}
		return null;
	}

	@Override
	public void ccAccelerometerChanged(float accelX, float accelY, float accelZ) {
		float deceleration = 0.4f;// 控制减速的速率(值越低=可以更快的改变方向)
		float sensitivity = 6.0f;// 加速计敏感度的值越大,主角精灵对加速计的输入就越敏感
		float maxVelocity = 100; // 最大速度值

		vel.x = vel.x * deceleration - accelX * sensitivity;
		vel.x /= 2.5f;
		if (vel.x > maxVelocity) {
			vel.x = maxVelocity;
		} else if (vel.x < -maxVelocity) {
			vel.x = -maxVelocity;
		}

		super.ccAccelerometerChanged(accelX, accelY, accelZ);

	}

	private void updatePos() {
		CGPoint point = sprite.getPosition();
		CGPoint next = CGPoint.ccpAdd(point, vel);
		// 限定飞机不能飞出屏幕
		if (next.x < 10.0f) {
			next.x = 10;
			vel = CGPoint.zero();
		}

		if (next.x > LAYER_WIDTH - 10.0f) {
			next.x = LAYER_WIDTH - 10.0f;
			vel = CGPoint.zero();
		}

		if (next.y < 10.0f) {
			next.y = 10.0f;
			vel = CGPoint.zero();
		}

		if (next.y > LAYER_HEIGHT - 10.0f) {
			next.y = LAYER_HEIGHT - 10.0f;
			vel = CGPoint.zero();
		}
		sprite.setPosition(next);
	}

	private CGPoint vel = new CGPoint();
	
	private void init() {
		// background
		back = CCSprite.sprite("game_bg.png");
		back.setAnchorPoint(CGPoint.zero());
		back.setScaleX(LAYER_WIDTH/back.getContentSize().getWidth());
		back.setScaleY(LAYER_HEIGHT/back.getContentSize().getHeight());
		addChild(back, 0);
		// spirte
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		sprite = CCSprite.sprite("bird1.png");
		CCNodeUtil.changeSizeWidthRatio(sprite, mScaleX, mScaleY);
		sprite.setPosition(CGPoint.ccp(winSize.width / 2,
				sprite.getContentSize().height / 2));
		addChild(sprite);

		initTimerBoard();
	}

	protected void addTarget() {

		CCSprite target = CCSprite.sprite("ball.png");

		// Determine where to spawn the target along the Y axis
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		int minX = (int) (target.getContentSize().width / 2.0f);
		int maxX = (int) (winSize.width - target.getContentSize().width / 2.0f);
		int rangeX = maxX - minX;
		int actualX = rand.nextInt(rangeX) + minX;

		// Create the target slightly off-screen along the right edge,
		// and along a random position along the Y axis as calculated above
		target.setPosition(actualX, winSize.height
				+ (target.getContentSize().height / 2.0f));
		CCNodeUtil.changeSizeWidthRatio(target, mScaleX, mScaleY);
		addChild(target);

		// Determine speed of the target
		int minDuration = 2;
		int maxDuration = 4;
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;

		// Create the actions
		CCMoveTo actionMove = CCMoveTo.action(actualDuration,
				CGPoint.ccp(actualX, -target.getContentSize().height / 2.0f));
		CCCallFuncN actionMoveDone = CCCallFuncN.action(this,
				"spriteMoveFinished");
		CCSequence actions = CCSequence.actions(actionMove, actionMoveDone);
		latestAddTargetTime = System.currentTimeMillis();
		mTargets.add(target);
		target.runAction(actions);
	}

	private long latestAddTargetTime = 0;

	private boolean isAddTargetAvailable() {
		return System.currentTimeMillis() - latestAddTargetTime > 300;
	}

	public void spriteMoveFinished(Object sender) {
		CCSprite sprite = (CCSprite) sender;
		this.removeChild(sprite, true);
		mTargets.remove(sprite);
	}
}
