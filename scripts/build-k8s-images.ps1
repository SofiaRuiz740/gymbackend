$ErrorActionPreference = "Stop"

$services = @(
    @{ Name = "api-gateway"; Dockerfile = "api-gateway/Dockerfile" },
    @{ Name = "user-service"; Dockerfile = "user-service/Dockerfile" },
    @{ Name = "category-service"; Dockerfile = "category-service/Dockerfile" },
    @{ Name = "product-service"; Dockerfile = "product-service/Dockerfile" },
    @{ Name = "inventory-service"; Dockerfile = "inventory-service/Dockerfile" },
    @{ Name = "report-service"; Dockerfile = "report-service/Dockerfile" }
)

foreach ($service in $services) {
    $image = "gym/$($service.Name):latest"
    Write-Host "Building $image"
    docker build -f $service.Dockerfile -t $image .
}

