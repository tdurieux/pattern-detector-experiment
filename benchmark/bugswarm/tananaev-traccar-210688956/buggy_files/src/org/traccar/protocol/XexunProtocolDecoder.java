/*
 * Copyright 2012 - 2016 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.protocol;

import org.jboss.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.helper.DateBuilder;
import org.traccar.helper.Parser;
import org.traccar.helper.PatternBuilder;
import org.traccar.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class XexunProtocolDecoder extends BaseProtocolDecoder {

    private final boolean full;

    public XexunProtocolDecoder(XexunProtocol protocol, boolean full) {
        super(protocol);
        this.full = full;
    }

    private static final Pattern PATTERN_BASIC = new PatternBuilder()
            .expression("G[PN]RMC,")
            .number("(?:(dd)(dd)(dd))?.(ddd),")   // time (hhmmss.sss)
            .expression("([AV]),")               // validity
            .number("(d*?)(d?d.d+),([NS]),")     // latitude
            .number("(d*?)(d?d.d+),([EW])?,")    // longitude
            .number("(d+.?d*),")                 // speed
            .number("(d+.?d*)?,")                // course
            .number("(?:(dd)(dd)(dd))?,")        // date (ddmmyy)
            .expression("[^*]*").text("*")
            .number("xx")                        // checksum
            .expression("\\r\\n").optional()
            .expression(",([FL]),")              // signal
            .expression("([^,]*),").optional()   // alarm
            .any()
            .number("imei:(d+),")                // imei
            .compile();

    private static final Pattern PATTERN_FULL = new PatternBuilder()
            .any()
            .number("(d+),")                     // serial
            .expression("([^,]+)?,")             // phone number
            .expression(PATTERN_BASIC.pattern())
            .number("(d+),")                     // satellites
            .number("(-?d+.d+)?,")               // altitude
            .number("[FL]:(d+.d+)V")             // power
            .any()
            .compile();

    private String decodeAlarm(String value) {
        if (value != null) {
            switch (value) {
                case "help me!":
                    return Position.ALARM_SOS;
                case "low battery":
                    return Position.ALARM_LOW_BATTERY;
                case "move!":
                case "moved!":
                    return Position.ALARM_MOVEMENT;
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        Pattern pattern = PATTERN_BASIC;
        if (full) {
            pattern = PATTERN_FULL;
        }

        Parser parser = new Parser(pattern, (String) msg);
        if (!parser.matches()) {
            return null;
        }

        Position position = new Position();
        position.setProtocol(getProtocolName());

        if (full) {
            position.set("serial", parser.next());
            position.set("number", parser.next());
        }

        DateBuilder dateBuilder = new DateBuilder()
                .setTime(parser.nextInt(), parser.nextInt(), parser.nextInt(), parser.nextInt());

        position.setValid(parser.next().equals("A"));
        position.setLatitude(parser.nextCoordinate());
        position.setLongitude(parser.nextCoordinate());
        position.setSpeed(parser.nextDouble());
        position.setCourse(parser.nextDouble());

        dateBuilder.setDateReverse(parser.nextInt(), parser.nextInt(), parser.nextInt());
        position.setTime(dateBuilder.getDate());

        position.set("signal", parser.next());
        position.set(Position.KEY_ALARM, decodeAlarm(parser.next()));

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        position.setDeviceId(deviceSession.getDeviceId());

        if (full) {
            position.set(Position.KEY_SATELLITES, parser.next().replaceFirst("^0*(?![\\.$])", ""));

            position.setAltitude(parser.nextDouble());

            position.set(Position.KEY_POWER, parser.nextDouble());
        }

        return position;
    }

}
