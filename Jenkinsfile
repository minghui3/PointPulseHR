pipeline {
    agent any

    environment {
        KUBECONFIG = credentials('KUBECONFIG')
        MAVEN_HOME = 'C:\\Program Files\\apache-maven-3.9.9\\'
        JAVA_HOME = 'C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.5.11-hotspot\\'
        PGHOST = 'localhost'
        PGUSER = 'postgres'
        PGPASSWORD = credentials('POSTGRES_PASSWORD')
        PGDATABASE = 'testdb'
        PATH = "${MAVEN_HOME}bin;${JAVA_HOME}bin;C:\\Windows\\System32;C:\\Program Files\\nodejs\\;C:\\Program Files\\Docker\\Docker\\resources\\bin;C:\\Windows\\System32\\WindowsPowerShell\\v1.0;C:\\Users\\quahm\\Downloads\\edgedriver_win64;C:\\Program Files (x86)\\Microsoft\\Edge\\Application"
        SELENIUM_HUB_URL = 'http://localhost:4444/wd/hub'
        MONGOURI = credentials('MONGOURI')
        MONGODB = 'PointPulseHR'
        MONGOCOLLECTION = 'test_results'
        CHROME_DRIVER_VERSION = '131.0.6778.69' 
        CHROME_DRIVER_PATH = "C:\\Program Files\\chromedriver-win64\\chromedriver.exe"
        EDGE_PATH = "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe"
        
    }

    stages {
        stage('Prepare/Checkout') {
            steps {
                // Clean workspace
                cleanWs()
                // Checkout first repo
                dir('first-repo'){
                    git branch: 'minghui3-patch-1', url: 'https://github.com/minghui3/PointPulseHR.git' 
                }

                // Checkout second repo
                dir('second-repo') {
                    git branch: 'minghui-test', url: 'https://github.com/minghui3/test-cases.git'
                }
            }
        }
        
        stage('Setup Selenium Grid') {
            steps {
                script {
                    dir('second-repo'){
                        // Stop and remove existing selenium-hub container if it's running
                        bat '''
                        docker ps -q -f name=selenium-hub | for /f %%i in ('more') do docker kill %%i
                        docker ps -aq -f name=selenium-hub | for /f %%i in ('more') do docker rm -f %%i
                        kubectl apply -f selenium-hub.yaml
                        kubectl apply -f chrome-node.yaml
                        kubectl apply -f firefox-node.yaml
                        kubectl apply -f edge-node.yaml
                        '''
                    }
                }
            }
        }
        
        stage('Scale Selenium Grid') {
            steps {
                script {
                    bat '''
                    echo Before Scaling:
                    kubectl get pods
                    echo Scaling Selenium Grid nodes...
                    kubectl scale deployment selenium-hub --replicas=3
                    kubectl scale deployment chrome-node --replicas=5
                    kubectl scale deployment edge-node --replicas=5
                    kubectl scale deployment firefox-node --replicas=5
                    echo After Scaling:
                    kubectl get pods
                    '''
                }
            }
        }
        
        stage('Install Dependencies') {
            steps {
                script {
                    dir('first-repo'){
                        // Install Node.js dependencies using npm on Windows
                        bat 'npm install'
                    }
                }
            }
        }

        stage('Initialize Database') {
            steps {
                script {
                    dir('first-repo'){
                        // Run the initdb script to set up the database on Windows
                        bat 'npm run initdb'
                    }
                }
            }
        }

        stage('Run Development Server') {
            steps {
                script {
                    dir('first-repo'){
                        // Run the development server on Windows
                        bat 'start /B npm run dev'
                    }
                }
            }
        }
        
        
        stage('Run Test Cases') {
            parallel {
                stage('Chrome Tests') {
                    steps {
                        script {
                            dir('second-repo'){
                                try {
                                bat """
                            \"${MAVEN_HOME}/bin/mvn\" test -Dbrowser=chrome -Dgrid.url=${SELENIUM_HUB_URL} -Dcucumber.plugin="json:target/cucumber-reports/chrome/chrome-report.json"
                            """
                                }   catch (Exception e) {
                                    echo "Chrome tests failed, but continuing the pipeline..."
                                } 
                            }
                        }
                    }
                }
                stage('Firefox Tests') {
                    steps {
                        script {
                            dir('second-repo'){
                                try {
                                bat """
                            \"${MAVEN_HOME}/bin/mvn\" test -Dbrowser=firefox -Dgrid.url=${SELENIUM_HUB_URL} -Dcucumber.plugin="json:target/cucumber-reports/firefox/firefox-report.json"
                            """
                                }   catch (Exception e) {
                                    echo "Firefox tests failed, but continuing the pipeline..."
                                } 
                            }
                        }
                    }
                }
                stage('Edge Tests') {
                    steps {
                        script {
                            dir('second-repo'){
                                try {
                                bat """
                            \"${MAVEN_HOME}/bin/mvn\" test -Dbrowser=edge -Dgrid.url=${SELENIUM_HUB_URL} -Dcucumber.plugin="json:target/cucumber-reports/edge/edge-report.json"
                            """
                                }   catch (Exception e) {
                                    echo "Edge tests failed, but continuing the pipeline..."
                                } 
                            }
                        }
                    }
                }
            }
        }
        
        stage('Add Test Results') {
            steps {
                script {
                    dir('second-repo'){
                        bat "mvn exec:java"
                    }
                }
            }
        }
        
        stage('Teardown Selenium Grid') {
            steps {
                script {
                    dir('second-repo'){
                        bat '''
                        echo "Starting Docker cleanup"
                        for /F "tokens=*" %%i in ('docker ps -q --filter "name=k8s_"') do docker kill %%i
                        for /F "tokens=*" %%i in ('docker ps -aq --filter "name=k8s_"') do docker rm -f %%i
                        '''
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
