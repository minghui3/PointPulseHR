pipeline {
    agent any

    environment {
        MAVEN_HOME = 'C:\\Program Files\\apache-maven-3.9.9\\'
        JAVA_HOME = 'C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.5.11-hotspot\\'
        PGHOST = 'localhost'
        PGUSER = 'postgres'
        PGPASSWORD = '03C283372u06'
        PGDATABASE = 'testdb'
        PATH = "${MAVEN_HOME}bin;${JAVA_HOME}bin;C:\\Windows\\System32;C:\\Program Files\\nodejs\\;C:\\Program Files\\Docker\\Docker\\resources\\bin;C:\\Windows\\System32\\WindowsPowerShell\\v1.0;C:\\Users\\quahm\\Downloads\\edgedriver_win64;C:\\Program Files (x86)\\Microsoft\\Edge\\Application"
        SELENIUM_HUB_URL = 'http://localhost:4444/wd/hub'
        MONGOURI = 'mongodb+srv://minghui3:QUAHM8758C@clusterfsdp.ut19z.mongodb.net/?retryWrites=true&w=majority&appName=clusterfsdp'
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
                    git branch: 'main', url: 'https://github.com/minghui3/PointPulseHR.git' 
                }

                // Checkout second repo
                dir('second-repo') {
                    git branch: 'expensivehippo', url: 'https://github.com/minghui3/test-cases.git'
                }
            }
        }
        
        stage('Test') {
            steps {
                echo "Testing Shell Script Execution"
            }
        }
        
        stage('Setup Selenium Grid') {
            steps {
                script {
                    dir('second-repo'){
                        
                        # Killing and removing existing Selenium Hub containers
                        docker ps -q -f name=selenium-hub | while read id; do docker kill "$id"; done
                        docker ps -aq -f name=selenium-hub | while read id; do docker rm -f "$id"; done
                    
                        # Restarting containers using docker-compose
                        docker-compose -f docker-compose.yml up -d
                    }
                }
            }
        }
        
        stage('Install Dependencies') {
            steps {
                script {
                    dir('first-repo'){
                        // Install Node.js dependencies using npm on Windows
                        npm install
                    }
                }
            }
        }

        stage('Initialize Database') {
            steps {
                script {
                    dir('first-repo'){
                        // Run the initdb script to set up the database on Windows
                        npm run initdb
                    }
                }
            }
        }

        stage('Run Development Server') {
            steps {
                script {
                    dir('first-repo'){
                        // Run the development server in background
                        sh '''#!/bin/bash
                        npm run dev &
                        '''
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
                        // Forcefully stop and remove the selenium-hub container
                        bat '''
                        docker ps -q -f name=selenium-hub | for /f %%i in ('more') do docker kill %%i
                        docker ps -aq -f name=selenium-hub | for /f %%i in ('more') do docker rm -f %%i
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
