package com.peiyu.rpcs.bios;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lipei05 on 2018/6/12.
 */
public class RpcServesrImpl implements IRpcServers {
    private int port;
    public static final HashMap<String, Class> serviceRegistry = new HashMap<String, Class>();
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 100, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(100));

    public RpcServesrImpl(int port) {
        this.port = port;
    }

    /**
     * 启动服务
     */
    @Override
    public void start() throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(port));
        System.out.println("Bio rpc 服务启动开始###端口###" + port);
        try {
            while (true) {
                System.out.println("等待客户端接入");
                executor.execute(new ServersTask(server.accept()));
            }
        } catch (Exception e) {
            if (server != null) {
                server.close();
            }
        }
    }

    @Override
    public void register(Class serviceInterface, Class impl) {
        serviceRegistry.put(serviceInterface.getName(), impl);
    }

    static class ServersTask implements Runnable {
        Socket client = null;

        public ServersTask(Socket socket) {
            this.client = socket;
        }

        ObjectInputStream input = null;//输入流
        ObjectOutputStream output = null;//输出流

        @Override
        public void run() {
            //接收客户端发送来的字节流,并转化成对象,反射调用服务实现者,获取执行结果
            try {
                input = new ObjectInputStream(this.client.getInputStream());
                String interfaceName = input.readUTF();
                String methodName = input.readUTF();
                Class<?>[] parameterType = (Class<?>[]) input.readObject();
                Object[] args = (Object[]) input.readObject();
                Class serverClass = serviceRegistry.get(interfaceName);
                if (serverClass == null) {
                    throw new ClassNotFoundException(interfaceName + " not found");
                }
                Method method = serverClass.getMethod(methodName, parameterType);
                Object result = method.invoke(serverClass.newInstance(), args);
                //执行结果反序列化返回给客户端
                output = new ObjectOutputStream(this.client.getOutputStream());
                output.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (this.client != null) {
                    try {
                        this.client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}
