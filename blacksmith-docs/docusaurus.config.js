// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Blacksmith Docs',
  tagline: 'Cars are cool',
  url: 'https://blacksmithftc.vercel.app',
  baseUrl: '/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/logo/blacksmith-logo.svg',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'RR9527', // Usually your GitHub org/user name.
  projectName: 'Blacksmith', // Usually your repo name.

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          routeBasePath: '/',
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl: 'https://github.com/toptobes/robot-code-v2/tree/master/blacksmith-docs/',
        },
        blog: false,
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
        sitemap: {
          changefreq: 'daily',
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      metadata: [
        { name: 'keywords', content: 'ftc, programming, framework, library' },
        { name: 'description', content: 'An intuitive framework for rapid FTC software development' },
      ],
      colorMode: {
        defaultMode: 'dark',
      },
      navbar: {
        title: 'Blacksmith v1.0.2',
        logo: {
          alt: 'Blacksmith Logo',
          src: 'img/logo/blacksmith-logo.svg',
        },
        items: [
          {
            type: 'doc',
            docId: 'overview',
            position: 'left',
            label: 'Docs',
          },
          {
            href: 'https://github.com/toptobes/robot-code-v2',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Docs',
            items: [
              {
                label: 'Overview',
                to: '/overview',
              },
              {
                label: 'Scheduler',
                to: '/scheduler-api/overview',
              },
              {
                label: 'Anvil',
                to: '/anvil/overview',
              }
            ],
          },
          {
            title: 'Contact me',
            items: [
              {
                label: 'My Github',
                href: 'https://github.com/toptobes',
              },
              {
                label: 'Sin#8551',
                to: 'https://discord.com',
              }
            ],
          },
          {
            title: 'Source code',
            items: [
              {
                label: 'GitHub',
                href: 'https://github.com/toptobes/robot-code-v2',
              },
            ],
          },
        ],
      },
      prism: {
        additionalLanguages: ['java', 'kotlin', 'brainfuck', 'groovy'],
        theme: require('prism-react-renderer/themes/github'),
        darkTheme: require('prism-react-renderer/themes/vsDark'),
        magicComments: [
          {
            className: 'theme-code-block-highlighted-line',
            line: 'highlight-next-line',
            block: {start: 'highlight-start', end: 'highlight-end'},
          },
          {
            className: 'code-block-error-line',
            line: 'this-will-error',
          },
        ],
      },
    }),

    plugins: [
      [
        '@docusaurus/plugin-pwa',
        {
          offlineModeActivationStrategies: [
            'appInstalled',
            'standalone',
            'queryString',
          ],
          pwaHead: [
            {
              tagName: 'link',
              rel: 'icon',
              href: '/img/logo/blacksmith-logo-square.png',
            },
            {
              tagName: 'link',
              rel: 'manifest',
              href: '/manifest.json', // your PWA manifest
            },
            {
              tagName: 'meta',
              name: 'theme-color',
              content: 'rgb(193,234,182)',
            },
            {
              tagName: 'meta',
              name: 'apple-mobile-web-app-capable',
              content: 'yes',
            },
            {
              tagName: 'meta',
              name: 'apple-mobile-web-app-status-bar-style',
              content: '#000',
            },
            {
              tagName: 'link',
              rel: 'apple-touch-icon',
              href: '/img/logo/blacksmith-logo-square.png',
            },
            {
              tagName: 'link',
              rel: 'mask-icon',
              href: '/img/logo/blacksmith-logo-square.svg',
              color: 'rgb(193,234,182)',
            },
            {
              tagName: 'meta',
              name: 'msapplication-TileImage',
              content: '/img/logo/blacksmith-logo-square.png',
            },
            {
              tagName: 'meta',
              name: 'msapplication-TileColor',
              content: '#000',
            },
          ],
        },
      ],
    ],
};

module.exports = config;
