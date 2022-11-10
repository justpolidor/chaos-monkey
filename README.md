# Chaos Monkey

This application is responsible for terminating running PODs in a given namespace to ensure that the business can tolerate and 
be resilient to failures.

## How it works

Chaos Monkey randomly deletes a running pod in the **workloads** namespace. It's a Spring Boot application running Java 17.
The image has been pushed on DockerHub
```bash
$ docker pull jpolidor/chaos-monkey:0.0.1-SNAPSHOT
```

## Usage

Deploy the ```manifests``` yamls in your cluster.

```bash
$ kubectl apply -Rf manifests/
```

The command above will deploy a cronjob and its associated resources; the cronjob starts every minute. In order to customize the scheduling, you can change:

```yaml
schedule: "* * * * *"
```

You can customize the following parameters by modifying the [config.yaml](https://github.com/justpolidor/chaos-monkey/manifests/config.yaml):
- **chaos-monkey.timeoutInSeconds**: specifies how many seconds to wait to contact the ApiServer until give up. **DEFAULT 30s**
- **chaos-monkey.gracePeriodInSeconds**: specifies how many seconds to wait, after the SIGTERM signal, to send the SIGKILL signal. By setting this value to 0 it forces the deletion (like _kubectl delete pod <pod-name> --force --grace-period=0_). **DEFAULT 30s**
- **chaos-monkey.labelSelector**: you can restrict the deletion to a subset of pods by labeling the pods you want to delete with a given label. For example by labeling a pod _"killable=yes"_, and by setting this property to _"killable=yes"_, chaos-monkey will delete only that pod. **DEFAULT null**

An example config:
```yaml
kind: ConfigMap
apiVersion: v1
metadata:
  name: chaos-monkey
  namespace: chaos-monkey
data:
  application.properties: |-
    chaos-monkey.timeoutInSeconds=30
    chaos-monkey.gracePeriodInSeconds=30
    chaos-monkey.labelSelector=killable=yes
```

## Testing

You can also test the application by running and/or modifying the [ChaosMonkeyApplicationTests.java](https://github.com/justpolidor/chaos-monkey/blob/main/src/test/java/it/justinpolidori/ChaosMonkey/ChaosMonkeyApplicationTests.java). This test class expects a running Kubernetes cluster on localhost on port 57401 with the given resources applied: it will retrieve the _chaos-monkey_ ServiceAccount token and use it to authenticate to the cluster; then you can declare your API as the following snippet:
```java
private static CoreV1Api api;
```
and execute the commands by leveraging the SA capabilities. 