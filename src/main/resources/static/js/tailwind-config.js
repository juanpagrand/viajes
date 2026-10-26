if (typeof tailwind !== 'undefined') {
    tailwind.config = {
        theme: {
            extend: {
                fontFamily: {
                    sans: ['"Outfit"', 'sans-serif'],
                },
                colors: {
                    background: '#020617',
                    surface: '#0f172a',
                    surfaceLight: '#1e293b',
                    primary: '#00C896',
                    primaryLight: '#33D9B0',
                    accent: '#3b82f6',
                },
                animation: {
                    'carousel': 'marquee 25s linear infinite',
                },
                keyframes: {
                    marquee: {
                        '0%': { transform: 'translateX(0%)' },
                        '100%': { transform: 'translateX(-50%)' },
                    }
                }
            }
        }
    };
}

