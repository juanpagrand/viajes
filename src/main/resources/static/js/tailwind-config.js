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
                primary: '#10b981',
                primaryLight: '#34d399',
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
}
