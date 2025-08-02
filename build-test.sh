#!/bin/bash

echo "🧪 Testing build and compilation..."

# Clean and compile
echo "📦 Cleaning and compiling..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful"
else
    echo "❌ Compilation failed"
    exit 1
fi

# Run tests
echo "🧪 Running tests..."
mvn test -DskipTests=false

if [ $? -eq 0 ]; then
    echo "✅ Tests passed"
else
    echo "❌ Tests failed"
    exit 1
fi

# Build package
echo "📦 Building package..."
mvn package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Package built successfully"
else
    echo "❌ Package build failed"
    exit 1
fi

echo "🎉 All build steps completed successfully!" 