    node {
       def mvnHome
       def workspace = pwd()
       def project_name = 'docker_demo-1.0-SNAPSHOT.jar'
       def project_log = 'docker_demo-1.0.log'
       def project_home = '/var/lib/jenkins/workspace/pipeline-test'
       def vm_ip = '192.168.248.102'
       def vm_port = '22'
       def vm_user = 'root'
       //代码获取
       stage('Preparation') { // for display purposes
    	  git branch: 'master',
    	  url:'https://github.com/dinghaoxiaoqin/docker-demo.git'
       }
       //构建
       stage('Build') {
          // Run the maven build
          if (isUnix()) {
             sh "mvn -Dmaven.test.skip=true clean package"

          } else {
             bat(/mvn -Dmaven.test.skip=true clean package/)
          }
       }

       //移动
       stage('MV') {
         sh "mv ${project_home}/target/${project_name} /usr/local/boot/"
       }

       //上传到服务器
       stage('Upload VM') {
         sh "scp -P ${vm_port} /usr/local/boot/${project_name} ${vm_user}@${vm_port}:/usr/local/boot/bk"
    	 //sh "ssh -p ${vm_port} ${vm_user}@${vm_ip} 'nohup java -jar /usr/local/src/${project_name} >> ${project_log} '"
       }


      //运行JAR包
      stage('Run') {
    	 //sh "if (ps -ef| grep java|grep ${project_name})then (ps -ef| grep java|grep ${project_name}| awk '{print \$2}'|xargs kill -9) fi"
         sh "ssh -p ${vm_port} ${vm_user}@${vm_ip} 'nohup java -jar /usr/local/boot/${project_name} >${project_log} 2>&1 &'"
      }
    }