/** @type {import('@docusaurus/types').DocusaurusConfig} */
module.exports = {
  title: 'World Generation Tutorial',
  tagline: 'A tutorial on world generation in Terasology',
  url: 'https://skaldarnar.github.io',
  baseUrl: '/TutorialWorldGeneration/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.ico',
  organizationName: 'skaldarnar', // Usually your GitHub org/user name.
  projectName: 'TutorialWorldGeneration', // Usually your repo name.
  themeConfig: {
    navbar: {
      title: 'World Generation Tutorial',
      logo: {
        alt: 'World Generation Tutorial Logo',
        src: 'img/logo.svg',
      },
      items: [
        {
          to: 'docs/',
          activeBasePath: 'docs',
          label: 'Docs',
          position: 'left',
        },
        {
          href: 'https://github.com/Terasology/TutorialWorldGeneration',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Community',
          items: [
            {
              label: 'Discord',
              href: 'https://discordapp.com/invite/terasology',
            },
            {
              label: 'Twitter',
              href: 'https://twitter.com/terasology',
            },
            {
              label: 'Reddit',
              href: 'https://reddit.com/r/terasology',
            },
            {
              label: 'Forum',
              href: 'https://forum.terasology.org',
            },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'terasology.org',
              to: 'https://terasology.org',
            },
            {
              label: 'GitHub',
              href: 'https://github.com/MovingBlocks/Terasology',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} The Terasology Foundation. Built with Docusaurus.`,
    },
    prism: {
      additionalLanguages: ['java', 'javastacktrace'],
    },
  },
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl:
            'https://github.com/terasology/TutorialWorldGeneration/edit/develop/docs/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      },
    ],
  ],
};
