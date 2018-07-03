package com.peiyu.rpcs.nios;

/**
 * Created by lipei05 on 2018/6/20.
 */
public interface IRpcServer {
    /**
     * 服务启动
     * @throws Exception
     */
     void start() throws Exception;

    /**
     * 服务关闭
     * @throws Exception
     */
     void stop() throws Exception;

    /**
     * 服务注册
     * @param serviceInterface 服务接口
     * @param impl 服务实现类
     */
    void register(Class serviceInterface, Class impl);
}
