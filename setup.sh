#!/bin/bash

###############################################################################
# Test Automation Framework - Quick Setup Script
# This script sets up the complete project structure and runs the application
###############################################################################

echo "╔══════════════════════════════════════════════════════════════╗"
echo "║     Test Automation Framework - Quick Setup Script          ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Check prerequisites
echo "Checking prerequisites..."
echo ""

# Check Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 11 ]; then
        print_success "Java $JAVA_VERSION found"
    else
        print_error "Java 11 or higher is required"
        exit 1
    fi
else
    print_error "Java is not installed"
    exit 1
fi

# Check Maven
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    print_success "Maven $MVN_VERSION found"
else
    print_error "Maven is not installed"
    exit 1
fi

# Check Chrome (optional, for UI tests)
if command -v google-chrome &> /dev/null || command -v chromium-browser &> /dev/null; then
    print_success "Chrome browser found"
else
    print_info "Chrome browser not found - UI tests may not work"
fi

echo ""
echo "Creating project structure..."
echo ""

# Create main directories
mkdir -p src/main/java/com/testautomation/{config,model,service,controller}
mkdir -p src/main/resources/{data,reports/cucumber,templates,static/{css,js,images}}
mkdir -p src/test/java/com/testautomation/{runners,stepdefinitions}
mkdir -p src/test/resources/{features/{api,ui},page-objects,test-data}
mkdir -p logs

print_success "Directory structure created"

# Create .gitkeep files to preserve empty directories
touch src/main/resources/data/.gitkeep
touch src/main/resources/reports/cucumber/.gitkeep
touch src/test/resources/features/.gitkeep
touch src/test/resources/page-objects/.gitkeep
touch src/test/resources/test-data/.gitkeep

print_success ".gitkeep files created"

# Initialize empty JSON files
echo "[]" > src/main/resources/data/tags.json
echo "[]" > src/main/resources/data/features.json
echo "[]" > src/main/resources/data/page-objects.json
echo "[]" > src/main/resources/data/test-data.json
echo '{"executions":[]}' > src/main/resources/data/execution-history.json

print_success "Data files initialized"

echo ""
echo "Building the project..."
echo ""

# Clean and build
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    print_success "Project built successfully"
else
    print_error "Build failed"
    exit 1
fi

echo ""
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║              Setup Completed Successfully! 🎉                ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo ""
echo "Next steps:"
echo ""
echo "1. Start the application:"
echo "   ${GREEN}mvn spring-boot:run${NC}"
echo ""
echo "2. Access the application:"
echo "   ${GREEN}http://localhost:8080${NC}"
echo ""
echo "3. Run tests:"
echo "   ${GREEN}mvn test${NC}"
echo ""
echo "4. Run tests with tags:"
echo "   ${GREEN}mvn test -Dcucumber.filter.tags=\"@smoke\"${NC}"
echo ""
echo "Would you like to start the application now? (y/n)"
read -r response

if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
    echo ""
    echo "Starting application..."
    echo ""
    mvn spring-boot:run
else
    echo ""
    print_info "Run 'mvn spring-boot:run' when you're ready to start"
    echo ""
fi