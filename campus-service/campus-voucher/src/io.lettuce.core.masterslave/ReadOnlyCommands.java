//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.lettuce.core.masterslave;

import io.lettuce.core.protocol.CommandType;
import io.lettuce.core.protocol.ProtocolKeyword;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

class ReadOnlyCommands {
    private static final Set<CommandType> READ_ONLY_COMMANDS = EnumSet.noneOf(CommandType.class);

    ReadOnlyCommands() {
    }

    // 如果此操作在READ_ONLY_COMMANDS（在下面static静态块中初始化）中，算读操作
    public static boolean isReadOnlyCommand(ProtocolKeyword protocolKeyword) {
        return READ_ONLY_COMMANDS.contains(protocolKeyword);
    }

    public static Set<CommandType> getReadOnlyCommands() {
        return Collections.unmodifiableSet(READ_ONLY_COMMANDS);
    }

    // READ_ONLY_COMMANDS集合通过静态块读取CommandName枚举类初始化的
    static {
        ReadOnlyCommands.CommandName[] var0 = ReadOnlyCommands.CommandName.values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            ReadOnlyCommands.CommandName commandNames = var0[var2];
            READ_ONLY_COMMANDS.add(CommandType.valueOf(commandNames.name()));
        }

    }

    static enum CommandName {
        ASKING,
        BITCOUNT,
        BITPOS,
        CLIENT,
        COMMAND,
        DUMP,
        ECHO,
        // eval 和 evalsha是redis执行脚本的方法，
        EXISTS,
        GEODIST,
        GEOPOS,
        GEORADIUS,
        GEORADIUS_RO,
        GEORADIUSBYMEMBER,
        GEORADIUSBYMEMBER_RO,
        GEOHASH,
        GET,
        GETBIT,
        GETRANGE,
        HEXISTS,
        HGET,
        HGETALL,
        HKEYS,
        HLEN,
        HMGET,
        HSCAN,
        HSTRLEN,
        HVALS,
        INFO,
        KEYS,
        LINDEX,
        LLEN,
        LRANGE,
        MGET,
        PFCOUNT,
        PTTL,
        RANDOMKEY,
        READWRITE,
        SCAN,
        SCARD,
        SCRIPT,
        SDIFF,
        SINTER,
        SISMEMBER,
        SMEMBERS,
        SRANDMEMBER,
        SSCAN,
        STRLEN,
        SUNION,
        TIME,
        TTL,
        TYPE,
        XINFO,
        XLEN,
        XPENDING,
        XRANGE,
        XREVRANGE,
        XREAD,
        ZCARD,
        ZCOUNT,
        ZLEXCOUNT,
        ZRANGE,
        ZRANGEBYLEX,
        ZRANGEBYSCORE,
        ZRANK,
        ZREVRANGE,
        ZREVRANGEBYLEX,
        ZREVRANGEBYSCORE,
        ZREVRANK,
        ZSCAN,
        ZSCORE;

        private CommandName() {
        }
    }
}


