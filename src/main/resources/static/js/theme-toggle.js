// Theme Toggle JavaScript
class ThemeManager {
    constructor() {
        this.currentTheme = localStorage.getItem('theme') || 'light';
        this.init();
    }

    init() {
        // Set initial theme
        this.setTheme(this.currentTheme);
        
        // Add theme toggle button to all pages
        this.addThemeToggle();
        
        // Add animation classes
        this.addAnimations();
    }

    setTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('theme', theme);
        this.currentTheme = theme;
        
        // Update theme toggle button icon
        this.updateToggleIcon();
    }

    toggleTheme() {
        const newTheme = this.currentTheme === 'light' ? 'dark' : 'light';
        this.setTheme(newTheme);
        
        // Add transition effect
        document.body.style.transition = 'all 0.3s ease';
        setTimeout(() => {
            document.body.style.transition = '';
        }, 300);
    }

    updateToggleIcon() {
        const toggleBtn = document.querySelector('.theme-toggle');
        if (toggleBtn) {
            toggleBtn.innerHTML = this.currentTheme === 'light' ? '🌙' : '☀️';
            toggleBtn.title = this.currentTheme === 'light' ? 'Switch to Dark Mode' : 'Switch to Light Mode';
        }
    }

    addThemeToggle() {
        // Check if toggle button already exists
        if (document.querySelector('.theme-toggle')) {
            return;
        }

        // Create theme toggle button
        const toggleBtn = document.createElement('button');
        toggleBtn.className = 'theme-toggle';
        toggleBtn.onclick = () => this.toggleTheme();
        
        // Add to page
        document.body.appendChild(toggleBtn);
        
        // Update icon
        this.updateToggleIcon();
    }

    addAnimations() {
        // Add fade-in animation to containers
        const containers = document.querySelectorAll('.auth-container, .profile-container');
        containers.forEach(container => {
            container.classList.add('fade-in');
        });

        // Add slide-in animation to form groups
        const formGroups = document.querySelectorAll('.form-group');
        formGroups.forEach((group, index) => {
            group.classList.add('slide-in');
            group.style.animationDelay = `${index * 0.1}s`;
        });
    }
}

// Initialize theme manager when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.themeManager = new ThemeManager();
});

// Add cursor prompts for interactive elements
document.addEventListener('DOMContentLoaded', () => {
    // Add cursor prompts for buttons
    const buttons = document.querySelectorAll('.btn, button');
    buttons.forEach(btn => {
        btn.style.cursor = 'pointer';
        btn.title = btn.textContent.trim() || 'Click me';
    });

    // Add cursor prompts for inputs
    const inputs = document.querySelectorAll('input, select, textarea');
    inputs.forEach(input => {
        input.style.cursor = 'text';
        if (input.type === 'email') {
            input.title = 'Enter your email address';
        } else if (input.type === 'password') {
            input.title = 'Enter your password';
        } else if (input.type === 'text') {
            input.title = 'Enter your ' + (input.name || 'information');
        }
    });

    // Add cursor prompts for links
    const links = document.querySelectorAll('a');
    links.forEach(link => {
        link.style.cursor = 'pointer';
        if (!link.title) {
            link.title = link.textContent.trim() || 'Click to navigate';
        }
    });

    // Add cursor prompts for select elements
    const selects = document.querySelectorAll('select');
    selects.forEach(select => {
        select.style.cursor = 'pointer';
        select.title = 'Choose an option';
    });
});

// Add loading states for forms
document.addEventListener('DOMContentLoaded', () => {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', (e) => {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.textContent = 'Processing...';
                
                // Re-enable button after 5 seconds as fallback
                setTimeout(() => {
                    submitBtn.disabled = false;
                    submitBtn.textContent = submitBtn.getAttribute('data-original-text') || 'Submit';
                }, 5000);
            }
        });
    });
});

// Add smooth scrolling for anchor links
document.addEventListener('DOMContentLoaded', () => {
    const anchorLinks = document.querySelectorAll('a[href^="#"]');
    anchorLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const target = document.querySelector(link.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
});

// Add keyboard navigation support
document.addEventListener('keydown', (e) => {
    // Toggle theme with Ctrl+T
    if (e.ctrlKey && e.key === 't') {
        e.preventDefault();
        if (window.themeManager) {
            window.themeManager.toggleTheme();
        }
    }
    
    // Focus management
    if (e.key === 'Tab') {
        document.body.classList.add('keyboard-navigation');
    }
});

// Remove keyboard navigation class on mouse use
document.addEventListener('mousedown', () => {
    document.body.classList.remove('keyboard-navigation');
});

// Add focus visible styles
document.addEventListener('DOMContentLoaded', () => {
    const style = document.createElement('style');
    style.textContent = `
        .keyboard-navigation :focus {
            outline: 2px solid var(--primary-color) !important;
            outline-offset: 2px !important;
        }
    `;
    document.head.appendChild(style);
}); 