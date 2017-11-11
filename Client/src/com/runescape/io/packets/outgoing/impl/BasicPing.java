package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.ByteBuffer;
import com.runescape.io.packets.outgoing.OutgoingPacket;

public class BasicPing implements OutgoingPacket {

	@Override
	public void buildPacket(ByteBuffer buf) {
		buf.putOpcode(0);
	}


}
