pipeline {
    agent any

    environment {
        MAVEN_HOME = 'C:\\Program Files\\apache-maven-3.9.9\\'
        JAVA_HOME = 'C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.5.11-hotspot\\'
        PGHOST = 'localhost'
        PGUSER = 'postgres'
        PGPASSWORD = ''
        PGDATABASE = 'testdb'
        PATH = "${MAVEN_HOME}bin;${JAVA_HOME}bin;C:\\Windows\\System32;C:\\Program Files\\nodejs\\;C:\\Program Files\\Docker\\Docker\\resources\\bin;C:\\Windows\\System32\\WindowsPowerShell\\v1.0"
        SELENIUM_HUB_URL = 'http://localhost:4444/wd/hub'
        MONGO_URI = 'mongodb+srv://minghui3:<password>@clusterfsdp.ut19z.mongodb.net/?retryWrites=true&w=majority&appName=clusterfsdp'
        MONGO_DB = 'PointPulseHR'
        CHROME_DRIVER_VERSION = '131.0.6778.69' 
        CHROME_DRIVER_PATH = "C:\\Program Files\\chromedriver-win64\\chromedriver.exe"
        
    }

    stages {
        stage('Checkout Code') {
            steps {
                script {
                    git url: 'https://github.com/minghui3/PointPulseHR.git', branch: 'ming-hui-branch'
                }
            }
        }
        
        stage('Setup Selenium Grid') {
            steps {
                script {
                    // Stop and remove existing selenium-hub container if it's running
                    bat '''
                    docker-compose -f docker-compose.yml up -d
                    '''
                }
            }
        }
        
        stage('Install Dependencies') {
            steps {
                script {
                    // Install Node.js dependencies using npm on Windows
                    bat 'npm install'
                }
            }
        }

        stage('Initialize Database') {
            steps {
                script {
                    // Run the initdb script to set up the database on Windows
                    bat 'npm run initdb'
                }
            }
        }

        stage('Run Development Server') {
            steps {
                script {
                    // Run the development server on Windows
                    bat 'start /B npm run dev'
                }
            }
        }
        
        stage('Run Test Cases') {
            parallel {
                stage('Chrome Tests') {
                    steps {
                        script {
                            try {
                                bat """
                                cd ${WORKSPACE}/maven
                                \"${MAVEN_HOME}/bin/mvn\" test -Dbrowser=chrome -Dgrid.url=${SELENIUM_HUB_URL} -Dcucumber.plugin=\"json:target/cucumber-reports/chrome-report.json,html:target/cucumber-reports/chrome-html-report.html\"
                                """
                            } catch (Exception e) {
                                echo "Chrome tests failed, but continuing the pipeline..."
                            } 
                        }
                    }
                }
                stage('Firefox Tests') {
                    steps {
                        script {
                            try {
                                bat """
                                cd ${WORKSPACE}/maven
                                \"${MAVEN_HOME}/bin/mvn\" clean install -Dbrowser=firefox -Dgrid.url=${SELENIUM_HUB_URL} -Dcucumber.plugin=\"json:target/cucumber-reports/firefox-report.json,html:target/cucumber-reports/firefox-html-report.html\"
                                """
                            } catch (Exception e) {
                                echo "Firefox tests failed, but continuing the pipeline..."
                            }
                        }
                    }
                }
                stage('Edge Tests') {
                    steps {
                        script {
                            try {
                                bat """
                                cd ${WORKSPACE}/maven
                               \"${MAVEN_HOME}/bin/mvn\" test -Dbrowser=edge -Dgrid.url=${SELENIUM_HUB_URL} -Dcucumber.plugin=\"json:target/cucumber-reports/edge-report.json,html:target/cucumber-reports/edge-html-report.html\"
                                """
                            } catch (Exception e) {
                                echo "Edge tests failed, but continuing the pipeline..."
                            }
                        }
                    }
                }
            }
        }
        stage('Push Test Results to MongoDB') {
            steps {
                script {
                    // Run MongoDB script to push test results
                    bat 'node pushTestResults.js'
                }
            }
        }
        stage('Teardown Selenium Grid') {
            steps {
                script {
                    // Forcefully stop and remove the selenium-hub container
                    bat '''
                    docker ps -q -f name=selenium-hub | for /f %%i in ('more') do docker kill %%i
                    docker ps -aq -f name=selenium-hub | for /f %%i in ('more') do docker rm -f %%i
                    '''
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
