package com.exactprosystems.ticker;

import javax.servlet.http.HttpSession;

import com.exactprosystems.ticker.channel.AbstractChannel;

public interface IChannelsListener {

	void onCreate(AbstractChannel channel, HttpSession session);

	void onClose(AbstractChannel channel, HttpSession session);

	void onLastClose(HttpSession session);

}
