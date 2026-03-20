$ErrorActionPreference = "Stop"

kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/platform-configmap.yaml
kubectl apply -f k8s/platform-secret.yaml
kubectl apply -f k8s/postgres-init-configmap.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/kafka.yaml
kubectl apply -f k8s/user-service.yaml
kubectl apply -f k8s/category-service.yaml
kubectl apply -f k8s/product-service.yaml
kubectl apply -f k8s/inventory-service.yaml
kubectl apply -f k8s/report-service.yaml
kubectl apply -f k8s/api-gateway.yaml

