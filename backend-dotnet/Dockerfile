# syntax=docker/dockerfile:1

# ---- build ----
FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src

# Restore (leverage layer caching)
COPY Directory.Build.props ./
COPY AlturaNova.slnx ./
COPY src/AlturaNova.Domain/AlturaNova.Domain.csproj src/AlturaNova.Domain/
COPY src/AlturaNova.Application/AlturaNova.Application.csproj src/AlturaNova.Application/
COPY src/AlturaNova.Infrastructure/AlturaNova.Infrastructure.csproj src/AlturaNova.Infrastructure/
COPY src/AlturaNova.Api/AlturaNova.Api.csproj src/AlturaNova.Api/
RUN dotnet restore src/AlturaNova.Api/AlturaNova.Api.csproj

# Build + publish
COPY . .
RUN dotnet publish src/AlturaNova.Api/AlturaNova.Api.csproj -c Release -o /app/publish /p:UseAppHost=false

# ---- runtime ----
FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS runtime
WORKDIR /app
COPY --from=build /app/publish .
ENV ASPNETCORE_URLS=http://+:8080
EXPOSE 8080
ENTRYPOINT ["dotnet", "AlturaNova.Api.dll"]
