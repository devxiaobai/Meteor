package com.xiaobai.meteor;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.types.CGPoint;

public class GameOverSprite extends CCSprite {

	private int score;

	private CCSprite back;

	private CCMenu menu;

	public GameOverSprite(int score) {
		this.score = score;
		CCSprite temp = CCSprite.sprite("gameover_frame.png");
		super.init(CCSpriteFrame.frame(temp.getTexture(),
				temp.getTextureRect(), CGPoint.zero()));
		temp.cleanup();
		initSprites();
	}

	private void initSprites() {
		back = CCSprite.sprite("gameover_frame.png");
		back.setAnchorPoint(CGPoint.zero());
		back.setPosition(CGPoint.zero());
		addChild(back);

		float bgWidth = back.getTextureRect().size.width;
		float bgHeight = back.getTextureRect().size.height;

		CCMenuItem retry = CCMenuItemImage.item("gameover_menu_retry.png",
				"gameover_menu_retry.png", this, "restart");
		float nWidth = retry.getBoundingBox().size.width;
		float nHeight = retry.getBoundingBox().size.height;
		retry.setAnchorPoint(CGPoint.zero());
		retry.setPosition(CGPoint.ccp(bgWidth / 2 - nWidth / 2,
				bgHeight / 2 + 30));
		CCNodeUtil.changeSizeWidthRatio(retry, getScaleX(), getScaleY(), 1);

		menu = CCMenu.menu(retry);
		menu.setAnchorPoint(CGPoint.ccp(0, 0));
		menu.setPosition(CGPoint.ccp(0, 0));
		addChild(menu);
	}

	public void restart(Object sender) {
		CCDirector.sharedDirector().replaceScene(GameLayer.scene());
	}
}
