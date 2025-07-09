#!/bin/bash

# Script de parada para el Sistema de Monitoreo Médico Distribuido
# Autor: Limones & Munarco

echo "🛑 Deteniendo Sistema de Monitoreo Médico Distribuido..."
echo "======================================================"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Detener microservicios Java (Spring Boot)
print_status "Deteniendo microservicios Java..."
java_pids=$(ps aux | grep 'spring-boot:run\|mvn.*spring-boot' | grep -v grep | awk '{print $2}')

if [ ! -z "$java_pids" ]; then
    echo "$java_pids" | xargs kill -15
    print_success "Microservicios Java detenidos"
    
    # Esperar un momento para shutdown graceful
    sleep 5
    
    # Forzar kill si aún están corriendo
    java_pids=$(ps aux | grep 'spring-boot:run\|mvn.*spring-boot' | grep -v grep | awk '{print $2}')
    if [ ! -z "$java_pids" ]; then
        print_warning "Forzando cierre de procesos restantes..."
        echo "$java_pids" | xargs kill -9
    fi
else
    print_status "No se encontraron microservicios Java ejecutándose"
fi

# Detener servicios Docker
print_status "Deteniendo servicios Docker..."
docker-compose down

if [ $? -eq 0 ]; then
    print_success "Servicios Docker detenidos correctamente"
else
    print_error "Error al detener servicios Docker"
fi

# Limpiar logs si existen
if [ -d "logs" ]; then
    print_status "Limpiando logs..."
    rm -rf logs/*.log
    print_success "Logs limpiados"
fi

# Mostrar estado final
print_status "Verificando que no queden procesos..."
remaining_processes=$(ps aux | grep -E 'spring-boot|eureka|gateway|patient|health|care|sincronizacion' | grep -v grep | wc -l)

if [ "$remaining_processes" -eq 0 ]; then
    print_success "Todos los procesos han sido detenidos correctamente"
else
    print_warning "Aún quedan $remaining_processes procesos relacionados ejecutándose"
    print_status "Para ver los procesos restantes ejecute: ps aux | grep -E 'spring-boot|eureka|gateway|patient|health|care|sincronizacion' | grep -v grep"
fi

print_success "Sistema de Monitoreo Médico Distribuido detenido"
echo ""
echo "💡 Para reiniciar el sistema ejecute: ./start-medical-system.sh"
echo "💡 Para limpiar completamente Docker ejecute: docker system prune -f"
