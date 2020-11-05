# paicoin-rpc-client 简介
  paicoin-rpc-client 是Project PAI区块链项目的java rpc 调用sdk。他可以方便您在java项目中调用Project PAI区块链对应的API<br/> 

- Blockchain：
- NetworkRPC：
- MiningRPC：
- WalletRPC：

# 如何使用<br/> 
- 下载项目代码并编译
    ```
        git clone https://github.com/paicoin/paicoin-rpc-client.git
    ```
    ```
        mvn install
    ```
- 在项目中添加依赖
    ```
    <dependency>
          <groupId>org.paicoin.rpcclient</groupId>
          <artifactId>paicoin-rpc-client</artifactId>
          <version>0.0.1-SNAPSHOT</version>
    </dependency>
    ```
- 配置RPC链接的服务器 

* Mac OS && Linux <br>
    1、复制 src/main/resources/paicoin.conf 至 当前用户目录/.paicoin/paicoin.conf<br>
    2、修改 当前用户目录/.paicoin/paicoin.conf 配置对应的paicoind 服务<br>
    
* Windows <br>
    1、复制 src/main/resources/paicoin.conf 至 AppData/Roaming/Paicoin/paicoin.conf <br>
    2、修改 AppData/Roaming/Paicoin/paicoin.conf 配置对应的paicoind 服务<br>


