#!/usr/bin/env groovy
pipeline {
    agent any

    //TODO Send mails on fail.
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out...'
                checkout scm
                //TODO Failed on failure
            }
        }
        stage('Build') {
            steps {
                echo 'Building...'
                sh './gradlew shadowJar'
                //TODO Failed on failure
            }
        }
        stage('Archive') {
            steps {
                echo 'Archiving...'
                archiveArtifacts artifacts: 'build/libs/*.jar', followSymlinks: false, onlyIfSuccessful: true
                //TODO Failed on failure
            }
        }
        stage('Deploy') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'liveServer', keyFileVariable: 'keyFile', passphraseVariable: 'passPhrase', usernameVariable: 'userName')]) {
                    def remote = [:]
                    remote.name = "vServer"
                    remote.host = "144.91.86.28"
                    remote.knownHosts = "144.91.86.28 ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBCin0HHDI6y+zpCujPp91LtiKB5JuZY3WxbATscfmZ3bnGv9E2IWQzo+p3uRUUq0GB/jL/B7G7JS3q8BXaXi6Os="
                    remote.user = userName
                    remote.passphrase = passPhrase
                    remote.identityFile = keyFile

                    echo 'Deploying...'
                    //TODO stop old service
                    //TODO delete old jar
                    sshPut remote: remote, from: 'build/libs/gainerbot-all.jar', into: '/home/deploy/gainerBot/'
                    sshScript remote: remote, script: '/home/deploy/gainerBot/start.sh'
                    //TODO Unstable on failure
                }
            }
        }
    }
}