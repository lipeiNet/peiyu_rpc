package com.peiyu.rpcs.bios;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 *
 * @author lipei05
 * @date 2018/6/14
 */
public class ProxyFactory<T> {

    public static <T> T create(final Class<?> serviceInterface, final String ip, final int port) {
        //将本地的接口调用转换成JDK的动态代理，在动态代理中实现接口的远程调用
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[]{serviceInterface}, new ProxyHandler(ip, port, serviceInterface));
    }

    static class ProxyHandler implements InvocationHandler {
        private String ip;
        private int port;
        private Class<?> serviceInterface;

        public ProxyHandler(String ip, int port, Class<?> serviceInterface) {
            this.ip = ip;
            this.port = port;
            this.serviceInterface = serviceInterface;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Socket socket = null;
            ObjectOutputStream output = null;
            ObjectInputStream input = null;
            try {
                // 创建Socket客户端，根据指定地址连接远程服务提供者
                socket = new Socket(ip, port);
                // 将远程服务调用所需的接口类、方法名、参数列表等编码后发送给服务提供者
                output = new ObjectOutputStream(socket.getOutputStream());
                output.writeUTF(serviceInterface.getName());
                output.writeUTF(method.getName());
                output.writeObject(method.getParameterTypes());
                output.writeObject(args);
                // 同步阻塞等待服务器返回应答，获取应答后返回
                input = new ObjectInputStream(socket.getInputStream());
                return input.readObject();
            } finally {
                if (socket != null) {
                    socket.close();
                }
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            }
        }
    }
}
