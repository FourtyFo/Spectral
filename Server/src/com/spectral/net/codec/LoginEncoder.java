package com.spectral.net.codec;

import com.spectral.net.login.LoginResponsePacket;
import com.spectral.net.login.LoginResponses;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Encodes login.
 * @author Swiffy
 */
public final class LoginEncoder extends MessageToByteEncoder<LoginResponsePacket> {

	@Override
	protected void encode(ChannelHandlerContext ctx, LoginResponsePacket msg, ByteBuf out)
			throws Exception {

		out.writeByte(msg.getResponse());

		if (msg.getResponse() == LoginResponses.LOGIN_SUCCESSFUL) {
			out.writeByte(msg.getRights().ordinal());
		}
	}
}
