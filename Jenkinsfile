#!/usr/bin/env groovy

def remote = [:]
remote.name = "vServer"
remote.host = "144.91.86.28"
remote.knownHosts = "144.91.86.28 ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBCin0HHDI6y+zpCujPp91LtiKB5JuZY3WxbATscfmZ3bnGv9E2IWQzo+p3uRUUq0GB/jL/B7G7JS3q8BXaXi6Os="

pipeline {
    agent any

    //TODO Send mails on fail.
    stages {
        stage('Build') {
            steps {
                echo 'Building...'
                sh 'chmod u+x gradlew'
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
                    remote.user = userName
                    remote.passphrase = passPhrase
                    remote.identityFile = keyFile

                    echo 'Deploying...'
                    sshCommand remote: remote, command: 'pwd'
                    /*
                    sshCommand remote: remote, command: 'sudo /bin/systemctl stop gainerbot'
                    sshCommand remote: remote, command: 'rm /home/deploy/gainerBot/gainerbot-all.jar'
                    sshPut remote: remote, from: 'build/libs/gainerbot-all.jar', into: '/home/deploy/gainerBot/'
                    sshCommand remote: remote, command: 'sudo /bin/systemctl start gainerbot'
                    //TODO Unstable on failure
                     */
                }
            }
        }
    }
}