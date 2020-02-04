<template>
  <q-layout view="hHh LpR fFf">

    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-btn dense flat round icon="menu" @click="left = !left" />

        <q-toolbar-title>
          cqpHamster
        </q-toolbar-title>

          <q-avatar square style="width:75px;height:80%;">
            <img src="statics/linglit_logo.png">
          </q-avatar>
      </q-toolbar>
    </q-header>

    <q-drawer show-if-above v-model="left" side="left" elevated>
          <q-scroll-area style="height: calc(100% - 200px); margin-top: 200px; border-right: 1px solid #ddd">
              <q-tree
                :nodes="menu"
                node-key="label"
                :selected.sync="selectedKey"
                @update:selected="changePage"
              />
          </q-scroll-area>

          <q-img class="absolute-top" src="statics/library.png" style="height: 200px">
            <div class="absolute-bottom bg-transparent q-gutter-md">
              <q-avatar size="50px" class="q-mb-sm" elevated>
                <img src="statics/questionmark.png">
              </q-avatar>
              <div>Not logged in</div>
            </div>
          </q-img>

    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>

  </q-layout>
</template>

<script>
export default {

  beforeMount() {
    let k_this = this;

    this.$axios.get('/internal/corpora/all').then(corpora => {
      corpora.data.forEach((corpus) => {
        let corpusMenu = {
          label: corpus.name,
          children: [
            {
              label: 'Concordances',
              selectable: true,
              handler: function() {
                k_this.$router.push('/corpora/' + corpus.id + '/concordances')
              }
            },
            {
              label: 'Clusters',
              selectable: true,
              handler: function() {
                k_this.$router.push('/corpora/' + corpus.id + '/clusters')
              }
            },
            {
              label: 'Zipf Stats',
              selectable: true,
              handler: function() {
                k_this.$router.push('/corpora/' + corpus.id + '/zipf')
              }
            },
            {
              label: 'Word Lists',
              selectable: true,
              handler: function() {
                k_this.$router.push('/corpora/' + corpus.id + '/word_lists')
              }
            },
            {
              label: 'Collocations',
              selectable: true,
              handler: function() {
                k_this.$router.push('/corpora/' + corpus.id + '/collocations')
              }
            },
            {
              label: 'Representation Learning',
              selectable: true,
              handler: function() {
                k_this.$router.push('/corpora/' + corpus.id + '/representation_learning')
              }
            },
            {
              label: 'Information',
              selectable: true,
              handler: function() {
                k_this.$router.push('/corpora/' + corpus.id + '/information')
              }
            }
          ]
        };

        this.menu[1].children.push(corpusMenu);
      });
    }).catch(err => {
      console.error(err);
    });
  },

  data () {
    let k_this = this;

    return {
      left: false,
      selectedKey:'',
      menu: [
        {
          label: 'Dashboard',
          selectable: true,
          handler: function() {
            k_this.$router.push('/dashboard')
          }
        },
        {
          label: 'Corpora',
          children: []
        },
        {
          label: 'Import',
          selectable: true,
              handler: function() {
                k_this.$router.push('/import')
              }
        },
        {
          label: 'Export',
          selectable: true,
              handler: function() {
                k_this.$router.push('/export')
              }
        },
        {
          label: 'User Groups',
          children: [
            {
              label: 'New User Group',
              selectable: true,
              handler: function() {
                k_this.$router.push('/user_groups/new')
              }
            },
            {
              label: 'My User Groups',
              children: []
            }
          ]
        },
        {
          label: 'User Preferences',
          selectable: true
        }
      ]
    }
  },
  methods: {
  }
}
</script>