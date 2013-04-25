package com.xiaobai.meteor;

import org.cocos2d.nodes.CCNode;

public class CCNodeUtil {
	/**
	 * 按原始比例调整
	 * @param node
	 * @param desiredWidth
	 * @param desiredHeight
	 */
	public static void changeSizeWidthRatio(CCNode node, float scaleX,
			float scaleY) {
		changeSizeWidthRatio(node, scaleX, scaleY, 1.0f);
	}
	
	/**
	 * 按原始比例调整
	 * @param node
	 * @param desiredWidth
	 * @param desiredHeight
	 * @param zoomScale 放缩倍数
	 */
	public static void changeSizeWidthRatio(CCNode node, float scaleX,
			float scaleY, float zoomScale) {
		if(node == null) {
			return;
		}
		float scale = scaleX < scaleY ? scaleX : scaleY;
		node.setScaleX(scale / scaleX * zoomScale);
		node.setScaleY(scale / scaleY * zoomScale);
	}
}
