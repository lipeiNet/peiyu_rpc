package com.peiyu.rpcs.bios;

import java.io.IOException;

/**
 * Created by lipei05 on 2018/6/14.
 */
public interface IRpcServers {
    /**
     * 启动rpc服务
     * @throws IOException
     */
    void start() throws IOException;

    /**
     * 服务注册
     * @param serviceInterface 服务接口
     * @param impl 服务实现类
     */
    void register(Class serviceInterface, Class impl);
}
